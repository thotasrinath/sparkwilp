package org.wilp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;
import org.apache.nifi.spark.NiFiDataPacket;
import org.apache.nifi.spark.NiFiReceiver;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.wilp.vo.OrderProductsVO;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by sthota on 3/26/17.
 */
public class StreamOrders {

    public static void main(String[] args) {

        try {
            SiteToSiteClientConfig config = new SiteToSiteClient.Builder()
                    .url("http://192.168.0.104:8090/nifi/")
                    .portName("wilporders")
                    .buildConfig();

            SparkConf sparkConf = new SparkConf().setMaster("local[10]").setAppName("NiFi-Spark Streaming WILP example");
            JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, new Duration(1000L));

            // Create a JavaReceiverInputDStream using a NiFi receiver so that we can pull data from
            // specified Port
            JavaReceiverInputDStream packetStream =
                    ssc.receiverStream(new NiFiReceiver(config, StorageLevel.MEMORY_AND_DISK()));

            // Map the data from NiFi to text, ignoring the attributes
            JavaDStream text = packetStream.map(new Function() {
                @Override
                public String call(final Object dataPacket) throws Exception {
                    return new String(((NiFiDataPacket) dataPacket).getContent(), StandardCharsets.UTF_8);
                }
            });

            // text.print();

            JavaDStream windowedText = text.window(org.apache.spark.streaming.Durations.minutes(3));


            JavaDStream<List<OrderProductsVO>> opDstream = windowedText.map(new Function<String, List<OrderProductsVO>>() {
                @Override
                public List<OrderProductsVO> call(String s) throws Exception {

                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<OrderProductsVO>>() {
                    }.getType();

                    List<OrderProductsVO> st = gson.fromJson(s, listType);


                    return st;
                }
            });



            opDstream.foreachRDD(new VoidFunction<JavaRDD<List<OrderProductsVO>>>() {

                Map<Long, HashSet<String>> map = new HashMap<Long, HashSet<String>>();
                List<Set<String>> itemsetList = new ArrayList<Set<String>>();

                @Override
                public void call(JavaRDD<List<OrderProductsVO>> listJavaRDD) throws Exception {

                    List<List<OrderProductsVO>> lvo = listJavaRDD.collect();

                    for (List<OrderProductsVO> opvl : lvo) {
                        for (OrderProductsVO opv : opvl) {
                            if (map.containsKey(opv.getOrder().getOrderId())) {
                                map.get(opv.getOrder().getOrderId()).add(opv.getProduct().getProductName());
                            } else {

                                HashSet<String> hs = new HashSet<String>();
                                hs.add(opv.getProduct().getProductName());
                                map.put(opv.getOrder().getOrderId(), hs);
                            }


                        }
                    }

                    AprioriFrequentItemsetGenerator<String> generator =
                            new AprioriFrequentItemsetGenerator<>();


                    for (Map.Entry<Long, HashSet<String>> entry : map.entrySet()) {
                        itemsetList.add(entry.getValue());

                     //   System.out.print("Item Set : " + itemsetList);
                    }

                    if (itemsetList.size() > 10) {
                        FrequentItemsetData<String> data = generator.generate(itemsetList, 0.2);
                        int i = 1;

                        for (Set<String> itemset : data.getFrequentItemsetList()) {
                            System.out.printf("%2d: %9s, support: %1.1f\n",
                                    i++,
                                    itemset,
                                    data.getSupport(itemset));
                        }
                       // itemsetList = new ArrayList<Set<String>>();

                    }


                }
            });


            //opDstream.print();
            ssc.start();

            ssc.awaitTermination();

        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}

