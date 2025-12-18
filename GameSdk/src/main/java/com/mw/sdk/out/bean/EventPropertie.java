package com.mw.sdk.out.bean;

import com.core.base.utils.GsonUtil;
import com.core.base.utils.SStringUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class EventPropertie implements Serializable {

    /**注册方式  ,文本*/
    String register_type;
    /**是否滚服  ,布尔*/
    boolean is_roll;
    /**上次登录时间  ,时间*/
    String last_login_time;
    /**本次离线时长(s)  ,数值*/
    int this_offline_time = -999;
    /**连续登录天数  ,数值*/
    int continuous_login_day = -999;
    /**累计登录天数  ,数值*/
    int total_login_day = -999;
    /**本次在线时长(s)  ,数值*/
    int this_online_time = -999;
    /**订单ID  ,文本*/
    String order_id;
    /**币种  ,文本*/
    String currency_type;
    /**支付金额  ,数值*/
    double pay_amount = -999;
    /**支付项目ID  ,数值*/
    String product_id;
    /**支付项目名称  ,文本*/
//    String payment_name;
    /**支付方式  ,文本*/
    String pay_method;
    /**是否首次  ,布尔*/
    boolean is_first;
    /**获得物品信息  ,对象组*/
    List<EventItemDetailBean> get_item_detail;
    /**失败原因  ,文本*/
    String fail_reason;
    /**变更原因  ,文本*/
    String change_reason;
    /**资产信息  ,对象组*/
    List<EventItemDetailBean> resource_info;
    /**引导类型  ,文本*/
    String guide_type;
    /**引导ID  ,数值*/
    String guide_id;
    /**引导小步骤ID  ,数值*/
    String guide_uid;
    /**邮件ID  ,文本*/
    String mail_id;
    /**邮件附件  ,对象组*/
    List<EventItemDetailBean> mail_content;
    /**任务类型  ,文本*/
    String task_type;
    /**任务ID  ,文本*/
    String task_id;
    /**任务名称  ,文本*/
    String task_name;
    /**消耗时间(s)  ,数值*/
    int use_time = -999;
    /**变动等级  ,数值*/
    String change_level;
    /**变动后等级  ,数值*/
    String after_level;
    /**活动类型  ,文本*/
    String activity_type;
    /**活动ID  ,文本*/
    String activity_id;
    /**icon名称  ,文本*/
    String icon_name;
    /**商城类型  ,文本*/
    String shop_type;
    /**商城ID  ,文本*/
    String shop_id;
    /**物品ID  ,数值*/
    String item_id;
    /**物品名称  ,文本*/
    String item_name;
    /**物品数量  ,数值*/
    int item_amount = -999;
    /**消耗货币名称  ,文本*/
    String cost_token_name;
    /**消耗货币ID  ,文本*/
    String cost_token_id;
    /**消耗货币数量  ,数值*/
    int cost_token_amount = -999;
    /**商城名称  ,文本*/
    String shop_name;
    /**点击位置  ,文本*/
    String click_position;
    /**是否免费  ,布尔*/
    boolean is_free;
    /**boss类型  ,文本*/
    String boss_type;
    /**bossID  ,文本*/
    String boss_id;
    /**是否胜利  ,布尔*/
    boolean is_win;
    /**公会ID  ,文本*/
    String guild_id;
    /**公会名称  ,文本*/
    String guild_name;
    /**公会等级  ,数值*/
    String guild_level;
    /**公会人数  ,数值*/
    int guild_member = -999;
    /**公会总战力  ,数值*/
    String guild_power;
    /**公会职位  ,文本*/
    String guild_position;
    /**目标玩家ID  ,文本*/
    String target_player_id;
    /**公会玩法类型  ,文本*/
    String guild_game_type;
    /**公会玩法ID  ,文本*/
    String guild_game_id;
    /**装备ID  ,文本*/
    String equipment_id;
    /**装备类型  ,文本*/
    String equipment_type;
    /**装备等级  ,数值*/
    String equipment_level;
    /**位置ID  ,数值*/
    String position_id;
    /**宝石ID  ,数值*/
    String gem_id;
    /**宝石等级  ,数值*/
    String gem_level;
    /**魂卡ID  ,文本*/
    String soulcard_id;
    /**消耗物品信息  ,对象组*/
    List<EventItemDetailBean> cost_item_detail;
    /**变动星级  ,数值*/
    String change_star;
    /**变动后星级  ,数值*/
    String after_star;
    /**战宠ID  ,文本*/
    String battlepet_id;
    /**战宠名称  ,文本*/
    String battlepet_name;
    /**战宠阶级  ,数值*/
    String battlepet_quality;
    /**战宠星级  ,数值*/
    String battlepet_star;
    /**变动阶级  ,数值*/
    String change_quality;
    /**变动后阶级  ,数值*/
    String after_quality;
    /**装扮ID  ,文本*/
    String costume_id;
    /**装扮类型  ,文本*/
    String costume_type;
    /**称号类型  ,文本*/
    String title_type;
    /**称号ID  ,文本*/
    String title_id;
    /**称号名称  ,文本*/
    String title_name;
    /**技能ID  ,文本*/
    String skill_id;
    /**技能名称  ,文本*/
    String skill_name;
    /**符石ID  ,文本*/
    String rune_id;
    /**怪物ID  ,文本*/
    String monster_id;
    /**npcID  ,文本*/
    String npc_id;

    /**区服id*/
    String server_id;
    /**平台*/
    String platform;
    /**在线人数*/
    int online_user = -999;

    /*排行榜类型*/
    String rank_type;
    /*排行信息*/
    List<EventRankInfoBean> rank_info;


    public JSONObject objToJsonObj() {

        JSONObject jsonObject = new JSONObject();
        Class c = this.getClass();
        while (c != null && c != EventPropertie.class.getSuperclass()) {

            Field[] fields = c.getDeclaredFields();
            Field.setAccessible(fields, true);

            for (int i = 0; i < fields.length; i++) {
                try {
                    Field xField = fields[i];
                    Object value = null;
                    if (xField.get(this) != null) {
                        value = xField.get(this);

                        if (xField.getType() == String.class) {
                            String smv = (String) value;
                           if (SStringUtil.isNotEmpty(xField.getName()) && SStringUtil.isNotEmpty(smv)){
                               try {
                                   jsonObject.put(xField.getName(), smv);
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }
                           }
                        } else if (xField.getType() == int.class) {
                            int ma = (int) value;
                            if (ma != -999){
                                try {
                                    jsonObject.put(xField.getName(), ma);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if (xField.getType() == double.class) {
                            double ma = (double) value;
                            if (ma != -999){
                                try {
                                    jsonObject.put(xField.getName(), ma);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if (xField.getType() == float.class) {
                            float ma = (float) value;
                            if (ma != -999){
                                try {
                                    jsonObject.put(xField.getName(), value);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else if (xField.getType() == boolean.class || xField.getType() == Boolean.class) {
                            boolean ma = (boolean) value;
                            try {
                                if (ma) {
                                    jsonObject.put(xField.getName(), ma);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (xField.getType() == List.class || xField.getType() == ArrayList.class){
                            try {
                                String objstr = GsonUtil.toJson(value);
                                if (SStringUtil.isNotEmpty(objstr)){
                                    jsonObject.put(xField.getName(), new JSONArray(objstr));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            c = c.getSuperclass();

        }
        return jsonObject;
    }


    public String getRegister_type() {
        return register_type;
    }

    public void setRegister_type(String register_type) {
        this.register_type = register_type;
    }

    public boolean isIs_roll() {
        return is_roll;
    }

    public void setIs_roll(boolean is_roll) {
        this.is_roll = is_roll;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public int getThis_offline_time() {
        return this_offline_time;
    }

    public void setThis_offline_time(int this_offline_time) {
        this.this_offline_time = this_offline_time;
    }

    public int getContinuous_login_day() {
        return continuous_login_day;
    }

    public void setContinuous_login_day(int continuous_login_day) {
        this.continuous_login_day = continuous_login_day;
    }

    public int getTotal_login_day() {
        return total_login_day;
    }

    public void setTotal_login_day(int total_login_day) {
        this.total_login_day = total_login_day;
    }

    public int getThis_online_time() {
        return this_online_time;
    }

    public void setThis_online_time(int this_online_time) {
        this.this_online_time = this_online_time;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCurrency_type() {
        return currency_type;
    }

    public void setCurrency_type(String currency_type) {
        this.currency_type = currency_type;
    }

    public double getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(double pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    //    public String getPayment_name() {
//        return payment_name;
//    }
//
//    public void setPayment_name(String payment_name) {
//        this.payment_name = payment_name;
//    }

    public String getPay_method() {
        return pay_method;
    }

    public void setPay_method(String pay_method) {
        this.pay_method = pay_method;
    }

    public boolean isIs_first() {
        return is_first;
    }

    public void setIs_first(boolean is_first) {
        this.is_first = is_first;
    }

    public List<EventItemDetailBean> getGet_item_detail() {
        return get_item_detail;
    }

    public void setGet_item_detail(List<EventItemDetailBean> get_item_detail) {
        this.get_item_detail = get_item_detail;
    }

    public String getFail_reason() {
        return fail_reason;
    }

    public void setFail_reason(String fail_reason) {
        this.fail_reason = fail_reason;
    }

    public String getChange_reason() {
        return change_reason;
    }

    public void setChange_reason(String change_reason) {
        this.change_reason = change_reason;
    }

    public List<EventItemDetailBean> getResource_info() {
        return resource_info;
    }

    public void setResource_info(List<EventItemDetailBean> resource_info) {
        this.resource_info = resource_info;
    }

    public String getGuide_type() {
        return guide_type;
    }

    public void setGuide_type(String guide_type) {
        this.guide_type = guide_type;
    }

    public String getGuide_id() {
        return guide_id;
    }

    public void setGuide_id(String guide_id) {
        this.guide_id = guide_id;
    }

    public String getGuide_uid() {
        return guide_uid;
    }

    public void setGuide_uid(String guide_uid) {
        this.guide_uid = guide_uid;
    }

    public String getMail_id() {
        return mail_id;
    }

    public void setMail_id(String mail_id) {
        this.mail_id = mail_id;
    }

    public List<EventItemDetailBean> getMail_content() {
        return mail_content;
    }

    public void setMail_content(List<EventItemDetailBean> mail_content) {
        this.mail_content = mail_content;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public int getUse_time() {
        return use_time;
    }

    public void setUse_time(int use_time) {
        this.use_time = use_time;
    }

    public String getChange_level() {
        return change_level;
    }

    public void setChange_level(String change_level) {
        this.change_level = change_level;
    }

    public String getAfter_level() {
        return after_level;
    }

    public void setAfter_level(String after_level) {
        this.after_level = after_level;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getIcon_name() {
        return icon_name;
    }

    public void setIcon_name(String icon_name) {
        this.icon_name = icon_name;
    }

    public String getShop_type() {
        return shop_type;
    }

    public void setShop_type(String shop_type) {
        this.shop_type = shop_type;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_amount() {
        return item_amount;
    }

    public void setItem_amount(int item_amount) {
        this.item_amount = item_amount;
    }

    public String getCost_token_name() {
        return cost_token_name;
    }

    public void setCost_token_name(String cost_token_name) {
        this.cost_token_name = cost_token_name;
    }

    public String getCost_token_id() {
        return cost_token_id;
    }

    public void setCost_token_id(String cost_token_id) {
        this.cost_token_id = cost_token_id;
    }

    public int getCost_token_amount() {
        return cost_token_amount;
    }

    public void setCost_token_amount(int cost_token_amount) {
        this.cost_token_amount = cost_token_amount;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getClick_position() {
        return click_position;
    }

    public void setClick_position(String click_position) {
        this.click_position = click_position;
    }

    public boolean isIs_free() {
        return is_free;
    }

    public void setIs_free(boolean is_free) {
        this.is_free = is_free;
    }

    public String getBoss_type() {
        return boss_type;
    }

    public void setBoss_type(String boss_type) {
        this.boss_type = boss_type;
    }

    public String getBoss_id() {
        return boss_id;
    }

    public void setBoss_id(String boss_id) {
        this.boss_id = boss_id;
    }

    public boolean isIs_win() {
        return is_win;
    }

    public void setIs_win(boolean is_win) {
        this.is_win = is_win;
    }

    public String getGuild_id() {
        return guild_id;
    }

    public void setGuild_id(String guild_id) {
        this.guild_id = guild_id;
    }

    public String getGuild_name() {
        return guild_name;
    }

    public void setGuild_name(String guild_name) {
        this.guild_name = guild_name;
    }

    public String getGuild_level() {
        return guild_level;
    }

    public void setGuild_level(String guild_level) {
        this.guild_level = guild_level;
    }

    public int getGuild_member() {
        return guild_member;
    }

    public void setGuild_member(int guild_member) {
        this.guild_member = guild_member;
    }

    public String getGuild_power() {
        return guild_power;
    }

    public void setGuild_power(String guild_power) {
        this.guild_power = guild_power;
    }

    public String getGuild_position() {
        return guild_position;
    }

    public void setGuild_position(String guild_position) {
        this.guild_position = guild_position;
    }

    public String getTarget_player_id() {
        return target_player_id;
    }

    public void setTarget_player_id(String target_player_id) {
        this.target_player_id = target_player_id;
    }

    public String getGuild_game_type() {
        return guild_game_type;
    }

    public void setGuild_game_type(String guild_game_type) {
        this.guild_game_type = guild_game_type;
    }

    public String getGuild_game_id() {
        return guild_game_id;
    }

    public void setGuild_game_id(String guild_game_id) {
        this.guild_game_id = guild_game_id;
    }

    public String getEquipment_id() {
        return equipment_id;
    }

    public void setEquipment_id(String equipment_id) {
        this.equipment_id = equipment_id;
    }

    public String getEquipment_type() {
        return equipment_type;
    }

    public void setEquipment_type(String equipment_type) {
        this.equipment_type = equipment_type;
    }

    public String getEquipment_level() {
        return equipment_level;
    }

    public void setEquipment_level(String equipment_level) {
        this.equipment_level = equipment_level;
    }

    public String getPosition_id() {
        return position_id;
    }

    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }

    public String getGem_id() {
        return gem_id;
    }

    public void setGem_id(String gem_id) {
        this.gem_id = gem_id;
    }

    public String getGem_level() {
        return gem_level;
    }

    public void setGem_level(String gem_level) {
        this.gem_level = gem_level;
    }

    public String getSoulcard_id() {
        return soulcard_id;
    }

    public void setSoulcard_id(String soulcard_id) {
        this.soulcard_id = soulcard_id;
    }

    public List<EventItemDetailBean> getCost_item_detail() {
        return cost_item_detail;
    }

    public void setCost_item_detail(List<EventItemDetailBean> cost_item_detail) {
        this.cost_item_detail = cost_item_detail;
    }

    public String getChange_star() {
        return change_star;
    }

    public void setChange_star(String change_star) {
        this.change_star = change_star;
    }

    public String getAfter_star() {
        return after_star;
    }

    public void setAfter_star(String after_star) {
        this.after_star = after_star;
    }

    public String getBattlepet_id() {
        return battlepet_id;
    }

    public void setBattlepet_id(String battlepet_id) {
        this.battlepet_id = battlepet_id;
    }

    public String getBattlepet_name() {
        return battlepet_name;
    }

    public void setBattlepet_name(String battlepet_name) {
        this.battlepet_name = battlepet_name;
    }

    public String getBattlepet_quality() {
        return battlepet_quality;
    }

    public void setBattlepet_quality(String battlepet_quality) {
        this.battlepet_quality = battlepet_quality;
    }

    public String getBattlepet_star() {
        return battlepet_star;
    }

    public void setBattlepet_star(String battlepet_star) {
        this.battlepet_star = battlepet_star;
    }

    public String getChange_quality() {
        return change_quality;
    }

    public void setChange_quality(String change_quality) {
        this.change_quality = change_quality;
    }

    public String getAfter_quality() {
        return after_quality;
    }

    public void setAfter_quality(String after_quality) {
        this.after_quality = after_quality;
    }

    public String getCostume_id() {
        return costume_id;
    }

    public void setCostume_id(String costume_id) {
        this.costume_id = costume_id;
    }

    public String getCostume_type() {
        return costume_type;
    }

    public void setCostume_type(String costume_type) {
        this.costume_type = costume_type;
    }

    public String getTitle_type() {
        return title_type;
    }

    public void setTitle_type(String title_type) {
        this.title_type = title_type;
    }

    public String getTitle_id() {
        return title_id;
    }

    public void setTitle_id(String title_id) {
        this.title_id = title_id;
    }

    public String getTitle_name() {
        return title_name;
    }

    public void setTitle_name(String title_name) {
        this.title_name = title_name;
    }

    public String getSkill_id() {
        return skill_id;
    }

    public void setSkill_id(String skill_id) {
        this.skill_id = skill_id;
    }

    public String getSkill_name() {
        return skill_name;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }

    public String getRune_id() {
        return rune_id;
    }

    public void setRune_id(String rune_id) {
        this.rune_id = rune_id;
    }

    public String getMonster_id() {
        return monster_id;
    }

    public void setMonster_id(String monster_id) {
        this.monster_id = monster_id;
    }

    public String getNpc_id() {
        return npc_id;
    }

    public void setNpc_id(String npc_id) {
        this.npc_id = npc_id;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getOnline_user() {
        return online_user;
    }

    public void setOnline_user(int online_user) {
        this.online_user = online_user;
    }

    public String getRank_type() {
        return rank_type;
    }

    public void setRank_type(String rank_type) {
        this.rank_type = rank_type;
    }

    public List<EventRankInfoBean> getRank_info() {
        return rank_info;
    }

    public void setRank_info(List<EventRankInfoBean> rank_info) {
        this.rank_info = rank_info;
    }
}
