package com.chestnut.TestFacePlusPlus.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/6/12 18:32
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class SearchBean {


    /**
     * request_id : 1497263205,19b4441c-916c-4b61-baaa-24ac4e5f2dce
     * time_used : 690
     * thresholds : {"1e-3":62.327,"1e-5":73.975,"1e-4":69.101}
     * results : [{"confidence":97.154,"user_id":"","face_token":"baafad213b042ae8dfe3a6e5e149d30b"}]
     */

    public String request_id;
    public int time_used;
    public ThresholdsBean thresholds;
    public List<ResultsBean> results;

    public static class ThresholdsBean {
        /**
         * 1e-3 : 62.327
         * 1e-5 : 73.975
         * 1e-4 : 69.101
         */

        @SerializedName("1e-3")
        public double _$1e3;
        @SerializedName("1e-5")
        public double _$1e5;
        @SerializedName("1e-4")
        public double _$1e4;
    }

    public static class ResultsBean {
        /**
         * confidence : 97.154
         * user_id :
         * face_token : baafad213b042ae8dfe3a6e5e149d30b
         */

        public double confidence;
        public String user_id;
        public String face_token;
    }
}
