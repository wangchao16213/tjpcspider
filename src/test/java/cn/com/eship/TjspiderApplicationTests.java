package cn.com.eship;

import cn.com.eship.scheduling.TestScheduling;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TjspiderApplicationTests {
    @Autowired
    TestScheduling testScheduling;

    @Test
    public void contextLoads() throws Exception{
        testScheduling.test1();

    }


//    public void scan(File file, List<Map<String, String>> list, String pId, String id) {
//        File[] files = file.listFiles();
//        Map<String, String> mapItem = new HashMap<>();
//        mapItem.put("id", id);
//        mapItem.put("pId", pId);
//        mapItem.put("name", file.getName());
//        list.add(mapItem);
//        if (files != null && files.length > 0) {
//            for (int i = 1; i <= files.length; i++) {
//                scan(files[i - 1], list, id, id + i + "");
//            }
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        List<Map<String, String>> list = new ArrayList<>();
//        TjspiderApplicationTests tjspiderApplicationTests = new TjspiderApplicationTests();
//        tjspiderApplicationTests.scan(new File("/Users/simon/goproject/dpigo"), list, "0", "1");
//        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println(objectMapper.writeValueAsString(list));
//    }

}
