package com.mw.sdk.out.bean;

import java.io.Serializable;

public class EventRankInfoBean implements Serializable {

//    取值样例：
//"rank_info":
//[{"id":"1000125","rank":1,"score":12000},{"id":"1000236","rank":2,"score":11005}]

    /*玩家id*/
    private String id;
    /*排名*/
    private int rank;
    /*得分*/
    private int score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
