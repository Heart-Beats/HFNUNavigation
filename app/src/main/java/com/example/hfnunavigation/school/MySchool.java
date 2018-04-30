package com.example.hfnunavigation.school;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.hfnunavigation.db.SchoolLocation;

import org.litepal.crud.DataSupport;
import java.util.List;

public class MySchool {

    public MySchool() {
    }

    /**
     * @return 合师范围
     */
    public LatLngBounds getHfnuRange() {
        LatLng southwest = new LatLng(31.753649, 117.232428);
        LatLng northeast = new LatLng(31.759782, 117.237562);
        return new LatLngBounds.Builder()
                .include(northeast)
                .include(southwest)
                .build();
    }

    /**
     * @return 合师的地点信息集合
     */
    public  List<SchoolLocation> getLocationsList() {
        //DataSupport.deleteAll(SchoolLocation.class);//删除所有数据，在这里主要配合完成更新数据
        List<SchoolLocation> locationsList = DataSupport.findAll(SchoolLocation.class);
        if (locationsList.isEmpty()) {
            initLocationsInfo();
            locationsList = DataSupport.findAll(SchoolLocation.class);
        }
        return locationsList;
    }

    /**
     * 初始化合师的各个地点信息
     */
    private void initLocationsInfo() {
        SchoolLocation xingZhiLou = new SchoolLocation();
        xingZhiLou.setLocationName("行知楼");
        xingZhiLou.setLatitude(31.75939);
        xingZhiLou.setLongitude(117.236762);
        xingZhiLou.setMoreInfo("学校办公楼，也是最高楼，各院老师的办公室都在此");
        xingZhiLou.save();

        SchoolLocation shiXunJiDi_1 = new SchoolLocation();
        shiXunJiDi_1.setLocationName("实训基地1");
        shiXunJiDi_1.setLatitude(31.758561);
        shiXunJiDi_1.setLongitude(117.236636);
        shiXunJiDi_1.setMoreInfo("大学生实训基地1，具体用途不知");
        shiXunJiDi_1.save();

        SchoolLocation shiXunJiDi_2 = new SchoolLocation();
        shiXunJiDi_2.setLocationName("实训基地2");
        shiXunJiDi_2.setLatitude(31.7585);
        shiXunJiDi_2.setLongitude(117.235855);
        shiXunJiDi_2.setMoreInfo("大学生实训基地1，具体用途不知");
        shiXunJiDi_2.save();

        SchoolLocation yiFuLou = new SchoolLocation();
        yiFuLou.setLocationName("逸夫楼");
        yiFuLou.setLatitude(31.757571);
        yiFuLou.setLongitude(117.236034);
        yiFuLou.setMoreInfo("逸夫教学楼，也称第二教学楼，也是期末考试和四六级考试地点之一");
        yiFuLou.save();

        SchoolLocation lvZhiLou = new SchoolLocation();
        lvZhiLou.setLocationName("履知楼");
        lvZhiLou.setLatitude(31.756972);
        lvZhiLou.setLongitude(117.236043);
        lvZhiLou.setMoreInfo("履知楼，生科和化院的实验楼");
        lvZhiLou.save();

        SchoolLocation tuShuGuan = new SchoolLocation();
        tuShuGuan.setLocationName("图书馆");
        tuShuGuan.setLatitude(31.75602);
        tuShuGuan.setLongitude(117.235927);
        tuShuGuan.setMoreInfo("图书馆，学生借阅书籍和考研自习场所，内藏各个门科书籍以及报纸杂志");
        tuShuGuan.save();

        SchoolLocation xiaoYiYuan = new SchoolLocation();
        xiaoYiYuan.setLocationName("校医院");
        xiaoYiYuan.setLatitude(31.755867);
        xiaoYiYuan.setLongitude(117.237121);
        xiaoYiYuan.setMoreInfo("校医院，地处树林之中，比较偏僻，普通疾病可以在此就医");
        xiaoYiYuan.save();

        SchoolLocation boYueLou = new SchoolLocation();
        boYueLou.setLocationName("博约楼");
        boYueLou.setLatitude(31.755084);
        boYueLou.setLongitude(117.236061);
        boYueLou.setMoreInfo("博约楼，也称第一教学楼，也是期末考试和四六级考试地点之一");
        boYueLou.save();

        SchoolLocation gongXingLou = new SchoolLocation();
        gongXingLou.setLocationName("躬行楼");
        gongXingLou.setLatitude(31.75696);
        gongXingLou.setLongitude(117.2348131);
        gongXingLou.setMoreInfo("躬行楼，计算机学院学生实验楼，也是计算机等级考试点");
        gongXingLou.save();

        SchoolLocation tingCheChang = new SchoolLocation();
        tingCheChang.setLocationName("停车场");
        tingCheChang.setLatitude(31.754942);
        tingCheChang.setLongitude(117.236915);
        tingCheChang.setMoreInfo("停车场，靠学校东门旁边");
        tingCheChang.save();

        SchoolLocation yinYueTing = new SchoolLocation();
        yinYueTing.setLocationName("音乐厅");
        yinYueTing.setLatitude(31.754355);
        yinYueTing.setLongitude(117.236515);
        yinYueTing.setMoreInfo("音乐厅，音乐学院教学楼");
        yinYueTing.save();

        SchoolLocation yiShuLou = new SchoolLocation();
        yiShuLou.setLocationName("艺术楼");
        yiShuLou.setLatitude(31.754113);
        yiShuLou.setLongitude(117.235639);
        yiShuLou.setMoreInfo("艺术楼，艺术传媒学院教学楼，内有很多学生作品");
        yiShuLou.save();

        SchoolLocation tiYuGuan = new SchoolLocation();
        tiYuGuan.setLocationName("体育馆");
        tiYuGuan.setLatitude(31.754105);
        tiYuGuan.setLongitude(117.234364);
        tiYuGuan.setMoreInfo("体育馆，体院教学场所，内有很多体育设施");
        tiYuGuan.save();

        SchoolLocation tianJingChang = new SchoolLocation();
        tianJingChang.setLocationName("田径场");
        tianJingChang.setLatitude(31.754577);
        tianJingChang.setLongitude(117.233088);
        tianJingChang.setMoreInfo("田径场，运动会、体测和新生军训场所");
        tianJingChang.save();

        SchoolLocation zhuXiTai = new SchoolLocation();
        zhuXiTai.setLocationName("主席台");
        zhuXiTai.setLatitude(31.754566);
        zhuXiTai.setLongitude(117.233672);
        zhuXiTai.setMoreInfo("主席台，开学典礼、运动会以及重大活动领导发言地");
        zhuXiTai.save();

        SchoolLocation lanQiuChang_1 = new SchoolLocation();
        lanQiuChang_1.setLocationName("篮球场1");
        lanQiuChang_1.setLatitude(31.754765);
        lanQiuChang_1.setLongitude(117.234364);
        lanQiuChang_1.setMoreInfo("篮球场1，篮球课教学场所，体院学子常在此打球");
        lanQiuChang_1.save();

        SchoolLocation heYuan = new SchoolLocation();
        heYuan.setLocationName("荷园");
        heYuan.setLatitude(31.755744);
        heYuan.setLongitude(117.233344);
        heYuan.setMoreInfo("荷园，一楼和二楼都为餐厅，三楼设有超市、打印室和移动联通代理点");
        heYuan.save();

        SchoolLocation liFaDian = new SchoolLocation();
        liFaDian.setLocationName("理发店");
        liFaDian.setLatitude(31.755898);
        liFaDian.setLongitude(117.232944);
        liFaDian.setMoreInfo("理发店，学校唯一的一家理发店，生意挺好");
        liFaDian.save();

        SchoolLocation yuShi = new SchoolLocation();
        yuShi.setLocationName("浴室");
        yuShi.setLatitude(31.755756);
        yuShi.setLongitude(117.232895);
        yuShi.setMoreInfo("浴室，一楼为男生浴室，二楼为女生浴室");
        yuShi.save();

        SchoolLocation zhuYuan = new SchoolLocation();
        zhuYuan.setLocationName("竹园");
        zhuYuan.setLatitude(31.756301);
        zhuYuan.setLongitude(117.233016);
        zhuYuan.setMoreInfo("竹园，一、二、三楼都为餐厅，但三楼有打印室，四楼为大学生活动中心");
        zhuYuan.save();

        SchoolLocation zhuYuanGuangChang = new SchoolLocation();
        zhuYuanGuangChang.setLocationName("竹园广场");
        zhuYuanGuangChang.setLatitude(31.756301);
        zhuYuanGuangChang.setLongitude(117.233537);
        zhuYuanGuangChang.setMoreInfo("竹园广场，举动各种学生活动的场所，傍晚时，常有人在此练轮滑");
        zhuYuanGuangChang.save();

        SchoolLocation heTang = new SchoolLocation();
        heTang.setLocationName("荷塘");
        heTang.setLatitude(31.756416);
        heTang.setLongitude(117.234377);
        heTang.setMoreInfo("荷塘，学校最大的一个池塘，塘边种有柳树，设有长椅，夏日开满荷花，风景怡人");
        heTang.save();

        SchoolLocation pingFang_1 = new SchoolLocation();
        pingFang_1.setLocationName("平房1");
        pingFang_1.setLatitude(31.756658);
        pingFang_1.setLongitude(117.23329);
        pingFang_1.setMoreInfo("平房1， 现为学生宿舍楼");
        pingFang_1.save();

        SchoolLocation pingFang_2 = new SchoolLocation();
        pingFang_2.setLocationName("平房2");
        pingFang_2.setLatitude(31.756811);
        pingFang_2.setLongitude(117.23329);
        pingFang_2.setMoreInfo("平房2， 现为学生宿舍楼");
        pingFang_2.save();

        SchoolLocation pingFang_3 = new SchoolLocation();
        pingFang_3.setLocationName("平房3");
        pingFang_3.setLatitude(31.756957);
        pingFang_3.setLongitude(117.233313);
        pingFang_3.setMoreInfo("平房3， 现为学生宿舍楼");
        pingFang_3.save();

        SchoolLocation suSheLou_1 = new SchoolLocation();
        suSheLou_1.setLocationName("1#");
        suSheLou_1.setLatitude(31.755625);
        suSheLou_1.setLongitude(117.234633);
        suSheLou_1.setMoreInfo("1#， 学生宿舍楼1栋");
        suSheLou_1.save();

        SchoolLocation suSheLou_2 = new SchoolLocation();
        suSheLou_2.setLocationName("2#");
        suSheLou_2.setLatitude(31.755633);
        suSheLou_2.setLongitude(117.234009);
        suSheLou_2.setMoreInfo("2#， 学生宿舍楼2栋");
        suSheLou_2.save();

        SchoolLocation suSheLou_3 = new SchoolLocation();
        suSheLou_3.setLocationName("3#");
        suSheLou_3.setLatitude(31.757275);
        suSheLou_3.setLongitude(117.233218);
        suSheLou_3.setMoreInfo("3#， 学生宿舍楼3栋");
        suSheLou_3.save();

        SchoolLocation suSheLou_4 = new SchoolLocation();
        suSheLou_4.setLocationName("4#");
        suSheLou_4.setLatitude(31.757901);
        suSheLou_4.setLongitude(117.233191);
        suSheLou_4.setMoreInfo("4#， 学生宿舍楼4栋");
        suSheLou_4.save();

        SchoolLocation suSheLou_5 = new SchoolLocation();
        suSheLou_5.setLocationName("5#");
        suSheLou_5.setLatitude(31.758573);
        suSheLou_5.setLongitude(117.233173);
        suSheLou_5.setMoreInfo("5#， 学生宿舍楼5栋");
        suSheLou_5.save();

        SchoolLocation suSheLou_6 = new SchoolLocation();
        suSheLou_6.setLocationName("6#");
        suSheLou_6.setLatitude(31.758891);
        suSheLou_6.setLongitude(117.233479);
        suSheLou_6.setMoreInfo("6#， 学生宿舍楼6栋");
        suSheLou_6.save();

        SchoolLocation suSheLou_7 = new SchoolLocation();
        suSheLou_7.setLocationName("7#");
        suSheLou_7.setLatitude(31.759359);
        suSheLou_7.setLongitude(117.233492);
        suSheLou_7.setMoreInfo("7#， 学生宿舍楼7栋");
        suSheLou_7.save();

        SchoolLocation suSheLou_8 = new SchoolLocation();
        suSheLou_8.setLocationName("8#");
        suSheLou_8.setLatitude(31.759382);
        suSheLou_8.setLongitude(117.234391);
        suSheLou_8.setMoreInfo("8#， 学生宿舍楼8栋");
        suSheLou_8.save();

        SchoolLocation suSheLou_9 = new SchoolLocation();
        suSheLou_9.setLocationName("9#");
        suSheLou_9.setLatitude(31.758872);
        suSheLou_9.setLongitude(117.234418);
        suSheLou_9.setMoreInfo("9#， 学生宿舍楼9栋");
        suSheLou_9.save();

        SchoolLocation suSheLou_10 = new SchoolLocation();
        suSheLou_10.setLocationName("10#");
        suSheLou_10.setLatitude(31.758427);
        suSheLou_10.setLongitude(117.234359);
        suSheLou_10.setMoreInfo("10#， 学生宿舍楼10栋");
        suSheLou_10.save();

        SchoolLocation xiaoMaiBu = new SchoolLocation();
        xiaoMaiBu.setLocationName("小卖部");
        xiaoMaiBu.setLatitude(31.758937);
        xiaoMaiBu.setLongitude(117.233223);
        xiaoMaiBu.setMoreInfo("小卖部， 内有各种零食和生活用品，因离宿舍楼近生意火爆");
        xiaoMaiBu.save();

        SchoolLocation kaiShuiFang = new SchoolLocation();
        kaiShuiFang.setLocationName("开水房");
        kaiShuiFang.setLatitude(31.759237);
        kaiShuiFang.setLongitude(117.233191);
        kaiShuiFang.setMoreInfo("开水房，周围摆满很多水瓶，忠心建议不要等晚自习放学时再打开水");
        kaiShuiFang.save();

        SchoolLocation xinJiangLou = new SchoolLocation();
        xinJiangLou.setLocationName("新疆楼");
        xinJiangLou.setLatitude(31.758062);
        xinJiangLou.setLongitude(117.235073);
        xinJiangLou.setMoreInfo("新疆楼，学校新疆班学生宿舍楼");
        xinJiangLou.save();

        SchoolLocation wangQiuChang = new SchoolLocation();
        wangQiuChang.setLocationName("网球场");
        wangQiuChang.setLatitude(31.757437);
        wangQiuChang.setLongitude(117.235028);
        wangQiuChang.setMoreInfo("网球场，原为篮球场，后改为网球场，但不对外免费开放");
        wangQiuChang.save();

        SchoolLocation pingPangQiuZhuo = new SchoolLocation();
        pingPangQiuZhuo.setLocationName("乒乓球桌");
        pingPangQiuZhuo.setLatitude(31.757625);
        pingPangQiuZhuo.setLongitude(117.23479);
        pingPangQiuZhuo.setMoreInfo("乒乓球桌，乒乓球课教学地点，网球场旁边，路边一排乒乓球桌");
        pingPangQiuZhuo.save();

        SchoolLocation zuQiuChang = new SchoolLocation();
        zuQiuChang.setLocationName("足球场");
        zuQiuChang.setLatitude(31.757682);
        zuQiuChang.setLongitude(117.234359);
        zuQiuChang.setMoreInfo("足球场(草坪)，足球课教学地点之一，另一个为田径足球场");
        zuQiuChang.save();

        SchoolLocation paiQiuChang = new SchoolLocation();
        paiQiuChang.setLocationName("排球场");
        paiQiuChang.setLatitude(31.758377);
        paiQiuChang.setLongitude(117.233627);
        paiQiuChang.setMoreInfo("排球场，排球课教学地点，常有女生在此打排球");
        paiQiuChang.save();

        SchoolLocation lanQiuChang_2 = new SchoolLocation();
        lanQiuChang_2.setLocationName("篮球场2");
        lanQiuChang_2.setLatitude(31.757832);
        lanQiuChang_2.setLongitude(117.233712);
        lanQiuChang_2.setMoreInfo("篮球场2，新生军训点之一，也常用作新生篮球比赛点，常有人打篮球");
        lanQiuChang_2.save();

        SchoolLocation xiaoShuLin = new SchoolLocation();
        xiaoShuLin.setLocationName("池塘小树林");
        xiaoShuLin.setLatitude(31.757287);
        xiaoShuLin.setLongitude(117.233959);
        xiaoShuLin.setMoreInfo("池塘小树林，内有一条林荫小道直通篮球场，有台阶，夜间行走需谨慎");
        xiaoShuLin.save();

        SchoolLocation yinXinShuLin = new SchoolLocation();
        yinXinShuLin.setLocationName("银杏树林");
        yinXinShuLin.setLatitude(31.758243);
        yinXinShuLin.setLongitude(117.23325);
        yinXinShuLin.setMoreInfo("银杏树林，4栋和5栋之间的树林，秋天时银杏树叶飘落一地，一片金黄煞是好看");
        yinXinShuLin.save();

        SchoolLocation xiaoDongMen = new SchoolLocation();
        xiaoDongMen.setLocationName("小东门");
        xiaoDongMen.setLatitude(31.758319);
        xiaoDongMen.setLongitude(117.237503);
        xiaoDongMen.setMoreInfo("小东门，学校出入口之一，锦绣大道公交站离此不远，5栋宿舍楼左右进校优先选择此地");
        xiaoDongMen.save();

        SchoolLocation dongMen = new SchoolLocation();
        dongMen.setLocationName("东门");
        dongMen.setLatitude(31.754731);
        dongMen.setLongitude(117.237476);
        dongMen.setMoreInfo("东门，学校正大门，外有六块大石刻有合肥师范学院");
        dongMen.save();

        SchoolLocation nanMen = new SchoolLocation();
        nanMen.setLocationName("南门");
        nanMen.setLatitude(31.753714);
        nanMen.setLongitude(117.235087);
        nanMen.setMoreInfo("南门，学校出入口之一，靠近小吃街，出入人数最多，门外道路来往车辆很多需注意安全");
        nanMen.save();

        SchoolLocation xiMen = new SchoolLocation();
        xiMen.setLocationName("西门");
        xiMen.setLatitude(31.755898);
        xiMen.setLongitude(117.232477);
        xiMen.setMoreInfo("西门，学校出入口之一，若去建大和小吃街也可以选择由此出去，门外来往车辆较少");
        xiMen.save();

        SchoolLocation xiaoBeiMen = new SchoolLocation();
        xiaoBeiMen.setLocationName("小北门");
        xiaoBeiMen.setLatitude(31.759736);
        xiaoBeiMen.setLongitude(117.2332);
        xiaoBeiMen.setMoreInfo("小北门，7栋旁边的一个大铁门，但常年处于封锁状态");
        xiaoBeiMen.save();

        SchoolLocation beiMen = new SchoolLocation();
        beiMen.setLocationName("北门");
        beiMen.setLatitude(31.759747);
        beiMen.setLongitude(117.235473);
        beiMen.setMoreInfo("北门，学校出入口之一，位于8栋旁边的可以选择由此出入学校");
        beiMen.save();
    }

}
