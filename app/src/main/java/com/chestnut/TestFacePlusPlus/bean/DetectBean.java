package com.chestnut.TestFacePlusPlus.bean;

import java.util.List;

/**
 * <pre>
 *     author: Chestnut
 *     blog  : http://www.jianshu.com/u/a0206b5f4526
 *     time  : 2017/6/12 17:15
 *     desc  :
 *     thanks To:
 *     dependent on:
 *     update log:
 * </pre>
 */
public class DetectBean {


    /**
     * image_id : 7fkxGlOwcPToSnlUosUVzQ==
     * request_id : 1497258778,87caf147-9c04-4f6e-be7d-907282012d35
     * time_used : 140
     * faces : [{"face_rectangle":{"width":124,"top":63,"left":137,"height":124},"face_token":"cad8b06138e0d7211af56ba83eadbe7f"},{"face_rectangle":{"width":121,"top":73,"left":11,"height":121},"face_token":"58e84e17514ec43225f6a181ddf26826"}]
     */

    public String image_id;
    public String request_id;
    public int time_used;
    public List<FacesBean> faces;

    public static class FacesBean {
        /**
         * face_rectangle : {"width":124,"top":63,"left":137,"height":124}
         * face_token : cad8b06138e0d7211af56ba83eadbe7f
         */

        public FaceRectangleBean face_rectangle;
        public String face_token;

        public static class FaceRectangleBean {
            /**
             * width : 124
             * top : 63
             * left : 137
             * height : 124
             */
            public int width;
            public int top;
            public int left;
            public int height;
        }
    }
}
