package com.github.simplesteph.protobuf;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import org.json.JSONObject;

import java.util.LinkedList;

import example.bubble.Test1Message;
import example.bubble.Test2Message;
import example.bubble.TestCommon;
import example.bubble.TestMerge;

/**
 * Test the proto to Json format(readable for edit/transmit/print/persist),
 * and then use method {mergeFrom} to put into one final message
 * */

public class MergeJsonMain {

    public static void main(String[] args) {

        System.out.println("Example for Merge");


        // create single message of merged message and put to Json string

        String test1Json = null;
        String test1_1Json = null;
        String test2Json = null;

        TestCommon.TestCommonMessage.Builder commonBuilder = TestCommon.TestCommonMessage.newBuilder();

        // message 1
        Test1Message.Builder test1builder = Test1Message.newBuilder();
        test1builder.setString1("aaa");
        commonBuilder.setTimestamp(System.currentTimeMillis());
        commonBuilder.setName(Test1Message.class.getName());
        test1builder.setTestCommonMessage(commonBuilder);

        TestMerge.TestMergeMessage.Builder mergeBuilder1 = TestMerge.TestMergeMessage.newBuilder();
        mergeBuilder1.addTest1Message(test1builder);
        try {
            test1Json = JsonFormat.printer().print(mergeBuilder1);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        // message 2
        Test1Message.Builder test1builder_1 = Test1Message.newBuilder();
        test1builder.setString1("bbb");
        commonBuilder.setTimestamp(System.currentTimeMillis());
        commonBuilder.setName(Test1Message.class.getName());
        test1builder_1.setTestCommonMessage(commonBuilder);

        TestMerge.TestMergeMessage.Builder mergeBuilder1_1 = TestMerge.TestMergeMessage.newBuilder();
        mergeBuilder1_1.addTest1Message(test1builder_1);
        try {
            test1_1Json = JsonFormat.printer().print(mergeBuilder1_1);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        // message 3
        Test2Message.Builder test2builder = Test2Message.newBuilder();
        test2builder.setString2("ccc");
        commonBuilder.setTimestamp(System.currentTimeMillis());
        commonBuilder.setName(Test2Message.class.getName());
        test2builder.setTestCommonMessage(commonBuilder);

        TestMerge.TestMergeMessage.Builder mergeBuilder2 = TestMerge.TestMergeMessage.newBuilder();
        mergeBuilder2.addTest2Message(test2builder);
        try {
            test2Json = JsonFormat.printer().print(mergeBuilder2);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        LinkedList<String> jsonList = new LinkedList<String>();
        jsonList.add(test1Json);
        jsonList.add(test1_1Json);
        jsonList.add(test2Json);


        /** Start merge */
        TestMerge.TestMergeMessage.Builder mergeAllBuilder = TestMerge.TestMergeMessage.newBuilder();

        int id = 9999;
        for (String j: jsonList) {
            // edit json string
            JSONObject obj = new JSONObject(j);
            String name = obj.keySet().iterator().next();
            JSONObject msgObj = obj.getJSONArray(name).getJSONObject(0);
            JSONObject common = msgObj.getJSONObject("testCommonMessage");
            common.put("id", id++);
            // System.out.println(obj.toString());

            // parse Json into protobuf
            TestMerge.TestMergeMessage.Builder builder = TestMerge.TestMergeMessage.newBuilder();
            try {
                JsonFormat.parser()
                        .ignoringUnknownFields()
                        .merge(obj.toString(), builder);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }

            // merge
             mergeAllBuilder.mergeFrom(builder.build());
        }

//        System.out.println("Final message:\n```\n" +  mergeAllBuilder + "```");
        try {
            System.out.println("Final message:\n```\n" +  JsonFormat.printer().print(mergeAllBuilder) + "```");
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

}
