package com.ctrip.framework.apollo.demo.api;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class SimpleApolloConfigDemo {
  private static final Logger logger = LoggerFactory.getLogger(SimpleApolloConfigDemo.class);
  private String DEFAULT_VALUE = "undefined";
  private Config config;
  private  Config jsonConfig;
  private static final Gson gson = new Gson();

  public SimpleApolloConfigDemo() {
    ConfigChangeListener changeListener = new ConfigChangeListener() {
      @Override
      public void onChange(ConfigChangeEvent changeEvent) {
        logger.info("Changes for namespace {}", changeEvent.getNamespace());
        for (String key : changeEvent.changedKeys()) {
          ConfigChange change = changeEvent.getChange(key);
          logger.info("Change - key: {}, oldValue: {}, newValue: {}, changeType: {}",
              change.getPropertyName(), change.getOldValue(), change.getNewValue(),
              change.getChangeType());
        }
      }
    };
    config = ConfigService.getAppConfig();
    //测试json格式的配置
    jsonConfig=ConfigService.getConfig("JsonNameSpace.json");
    config.addChangeListener(changeListener);
    jsonConfig.addChangeListener(changeListener);
  }

  private String getConfig(String key) {
    String result = config.getProperty(key, DEFAULT_VALUE);
    logger.info(String.format("Loading key : %s with value: %s", key, result));
    return result;
  }

  //获取json配置中的属性
  private String getJsonConfig(String key) {
    String jsonString = jsonConfig.getProperty("content",DEFAULT_VALUE);
    Map<String,String> configMap=gson.fromJson(jsonString, Map.class);
    String result=configMap.get(key);
    logger.info(String.format("Loading json key : %s with value: %s", key, result));
    return result;
  }

  public static void main(String[] args) throws IOException {
    SimpleApolloConfigDemo apolloConfigDemo = new SimpleApolloConfigDemo();
    System.out.println(
        "Apollo Config Demo. Please input key to get the value. Input quit to exit.");
    while (true) {
      System.out.print("> ");
      String input = new BufferedReader(new InputStreamReader(System.in, Charsets.UTF_8)).readLine();
      if (input == null || input.length() == 0) {
        continue;
      }
      input = input.trim();
      if (input.equalsIgnoreCase("quit")) {
        System.exit(0);
      }
      apolloConfigDemo.getConfig(input);
      apolloConfigDemo.getJsonConfig(input);
    }
  }
}
