package cn.com.eship.scheduling;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Queue;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestScheduling {
    protected static Logger logger = LoggerFactory.getLogger(TestScheduling.class);
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private Queue queue;

    @Scheduled(cron = "0 0/59 * * * ?")
    public void test1() {
        logger.info("爬虫开始调度 " + new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date1 = new Date();
        logger.info("爬虫开始访问目标");
        Document document = null;
        try {
            document = Jsoup.connect("http://www.oie.int/wahis_2/public/wahid.php/Countryinformation/Countryreports").timeout(60000)
                    .data("country_select_method", "2", "multipleselectregion", "6", "totalregion", "7", "add", "country", "year", sdf.format(date1)).post();
        } catch (IOException e) {
            logger.error("访问目标错误", e);
            return;
        }
        logger.info("开始解析网页");
        Elements trElements = document.getElementsByClass("TableContent table-pad0-100").first().getElementsByTag("tr");
        logger.info("需要抓取" + trElements.size());
        for (int i = 0; i < trElements.size(); i++) {
            try {
                //跳过表头
                if (i > 0) {
                    logger.info("当前" + i);
                    Map<String, Object> item = new HashMap<String, Object>();
                    Elements tdElements = trElements.get(i).getElementsByTag("td");
                    String report_link = tdElements.get(0).getElementsByTag("a").first().attr("href");
                    int reportIdIndex = report_link.indexOf("reportid=");
                    String report_id = report_link.substring(reportIdIndex + 9, report_link.length());
                    report_link = "http://www.oie.int/wahis_2/public/wahid.php/Reviewreport/Review?page_refer=MapFullEventReport&reportid=" + report_id;
                    String country = getContent(tdElements.get(2).text(), "");
                    String date = getDate(tdElements.get(3).text().trim());
                    String disease = getContent(tdElements.get(4).text(), "");
                    String reason_for_notification = getContent(tdElements.get(5).text(), "");
                    String disease_manifestation = getContent(tdElements.get(6).text(), "");
                    int outbreaks = Integer.valueOf(getContent(tdElements.get(7).text(), "0"));
                    String date_resolved = getContent(tdElements.get(8).text(), "");
                    String html = getHtmlContent(report_link);
                    item.put("reportId", report_id);
                    item.put("reportLink", report_link);
                    item.put("country", country);
                    item.put("date", date);
                    item.put("disease", disease);
                    item.put("reasonForNotification", reason_for_notification);
                    item.put("diseaseManifestation", disease_manifestation);
                    item.put("outbreaks", outbreaks);
                    item.put("dateResolved", date_resolved);
                    item.put("html", html);
                    String itemJson = JSON.toJSONString(item);
                    this.jmsMessagingTemplate.convertAndSend(this.queue, itemJson);
                }

            } catch (Exception e) {
                logger.error("爬虫无法获取目标网页资源", e);
                continue;
            }
        }


    }


    private String getContent(String value, String defaultValue) {
        if (StringUtils.isNotBlank(value)) {
            return value.trim();
        }
        return defaultValue;
    }


    private String getDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            return "";
        }
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat2.format(date1);
    }

    private String getHtmlContent(String url) throws Exception {
        Document document = Jsoup.connect(url).timeout(60000).get();
        String html = document.getElementsByClass("ContentBigTableWhite").html();
        return html;
    }
}
