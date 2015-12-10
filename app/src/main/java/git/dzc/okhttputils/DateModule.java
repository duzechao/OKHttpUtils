package git.dzc.okhttputils;

/**
 * Created by zajo on 15/12/10.
 */
public class DateModule {
    /**
     * success : 1
     * result : {"timestamp":"1449734277","datetime_1":"2015-12-10 15:57:57","datetime_2":"2015年12月10日 15时57分57秒","week_1":"4","week_2":"星期四","week_3":"周四","week_4":"Wednesday"}
     */

    private String success;
    /**
     * timestamp : 1449734277
     * datetime_1 : 2015-12-10 15:57:57
     * datetime_2 : 2015年12月10日 15时57分57秒
     * week_1 : 4
     * week_2 : 星期四
     * week_3 : 周四
     * week_4 : Wednesday
     */

    private ResultEntity result;

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }

    public String getSuccess() {
        return success;
    }

    public ResultEntity getResult() {
        return result;
    }

    public static class ResultEntity {
        private String timestamp;
        private String datetime_1;
        private String datetime_2;
        private String week_1;
        private String week_2;
        private String week_3;
        private String week_4;

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public void setDatetime_1(String datetime_1) {
            this.datetime_1 = datetime_1;
        }

        public void setDatetime_2(String datetime_2) {
            this.datetime_2 = datetime_2;
        }

        public void setWeek_1(String week_1) {
            this.week_1 = week_1;
        }

        public void setWeek_2(String week_2) {
            this.week_2 = week_2;
        }

        public void setWeek_3(String week_3) {
            this.week_3 = week_3;
        }

        public void setWeek_4(String week_4) {
            this.week_4 = week_4;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getDatetime_1() {
            return datetime_1;
        }

        public String getDatetime_2() {
            return datetime_2;
        }

        public String getWeek_1() {
            return week_1;
        }

        public String getWeek_2() {
            return week_2;
        }

        public String getWeek_3() {
            return week_3;
        }

        public String getWeek_4() {
            return week_4;
        }
    }
}
