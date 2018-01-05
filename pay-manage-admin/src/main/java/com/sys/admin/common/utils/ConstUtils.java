package com.sys.admin.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统常量定义
 */
public class ConstUtils {
    /**
     * 平台机构ID
     */
    public static final Long ROOT_OFFICE_ID = 1L;

    /**
     * 超级管理员ID
     */
    public static final Long ROOT_ADMIN_ID = 1L;

    /**
     * 是
     */
    public static final String YES = "1";

    /**
     * 否
     */
    public static final String NO = "0";

    /**
     * 显示
     */
    public static final String DISPLAY_SHOW = "1";

    /**
     * 隐藏
     */
    public static final String DISPLAY_HIDE = "0";

    /**
     * 正常
     */
    public static final String NORMAL = "0";

    /**
     * 异常 删除
     */
    public static final String BLOCK = "1";

    /**
     * 拼接符
     */
    public static final String SPLIT_CHAR = ",";

    /**
     * 水印图片地址
     */
    public static final String WATERMARK_IMAGE_PATH = "/sy.png";

    /**
     * 图片水印开关 -- 开启
     */
    public static final String WATERMARK_SWITCH_ON = "1";
    /**
     * 图片水印开关 -- 关闭
     */
    public static final String WATERMARK_SWITCH_OFF = "0";

    /**
     * 图片水印位置 -- 左上
     */
    public static final int WATERMARK_POSITION_TOP_LEFT = 1;
    /**
     * 图片水印位置 -- 右上
     */
    public static final int WATERMARK_POSITION_TOP_RIGHT = 2;
    /**
     * 图片水印位置 -- 左下
     */
    public static final int WATERMARK_POSITION_BOTTOM_LEFT = 3;
    /**
     * 图片水印位置 -- 右下(默认)
     */
    public static final int WATERMARK_POSITION_BOTTOM_RIGHT = 4;
    /**
     * 图片水印位置 -- 居中
     */
    public static final int WATERMARK_POSITION_CENTER = 5;

    /**
     * 统计日期的类型 -- 年
     */
    public static final String STAT_DATE_TYPE_YEAR = "1";
    /**
     * 统计日期的类型 -- 月
     */
    public static final String STAT_DATE_TYPE_MONTH = "2";
    /**
     * 统计日期的类型 -- 周
     */
    public static final String STAT_DATE_TYPE_WEEK = "3";
    /**
     * 统计日期的类型 -- 日
     */
    public static final String STAT_DATE_TYPE_DAY = "4";

    /**
     * 分平台/提供商常量
     */
    public static final class PROVIDER_CONST {
        /**
         * 分平台/提供商是否开通 -- 未开通
         */
        public static final String FLAG_CLOSE = "0";

        /**
         * 分平台/提供商是否开通 -- 开通
         */
        public static final String FLAG_OPEN = "1";

        /**
         * 分平台/提供商支付方式 -- 未开通
         */
        public static final String PAYTYPE_CLOSE = "0";

        /**
         * 分平台/提供商支付方式 -- 支付宝
         */
        public static final String PAYTYPE_ALIPAY = "1";
    }

    /**
     * 消息常量
     */
//    public static final class MESSAGE_CONST {
//        /**
//         * 推送消息
//         */
//        public static final String ACTIVITY = "1";
//        /**
//         * 项目消息
//         */
//        public static final String PROJECT = "2";
//        /**
//         * 订单消息
//         */
//        public static final String ORDER = "3";
//
//
//        /**
//         * 状态 -- -未推送
//         */
//        public static final String PUSH_PENDING = "0";
//        /**
//         * 状态 -- 已推送
//         */
//        public static final String PUSH_SUCCESS = "1";
//        /**
//         * 状态 -- 已结束
//         */
//        public static final String PUSH_OVER = "2";
//
//        /**
//         * 消息类型 -- 系统消息
//         */
//        public static final String TYPE_SYSTEM = "1";
//
//        /**
//         * 消息类型 -- 系统通知（后台管理员发送）
//         */
//        public static final String TYPE_NOTICE = "2";
//
//        /**
//         * 消息类型 -- 站内消息（后台管理员/会员发送）
//         */
//        public static final String TYPE_MESSAGE = "3";
//
//        /**
//         * 消息发送人类型 -- 系统消息
//         */
//        public static final String SENDER_TYPE_SYSTEM = "1";
//
//        /**
//         * 消息发送人类型 -- 后台管理员
//         */
//        public static final String SENDER_TYPE_ADMIN = "2";
//
//        /**
//         * 消息发送人类型 -- 站内消息（会员间发送）
//         */
//        public static final String SENDER_TYPE_CUSTOMER = "3";
//    }

    /**
     * 消息常量
     */
    public static final class REL_CONST {

        /**
         * 状态 -- -未读
         */
        public static final String UNREAD = "0";
        /**
         * 状态 -- 已读
         */
        public static final String READ = "1";
        /**
         * 状态 -- 删除
         */
        public static final String DEL = "2";
    }

    /**
     * 审核常量
     */
//    public static final class CHECK_CONST {
//        /**
//         * 状态 -- -待审核
//         */
//        public static final String STATUS_CHECK_PENDING = "0";
//        /**
//         * 状态 -- 审核通过
//         */
//        public static final String STATUS_CHECK_SUCCESS = "1";
//        /**
//         * 状态 -- 审核不通过
//         */
//        public static final String STATUS_CHECK_FAIL = "2";
//
//        /**
//         * 审核对象类型 -- 机构
//         */
//        public static final String OBJECT_TYPE_OFFICE = "office";
//        /**
//         * 审核对象类型 -- 项目
//         */
//        public static final String OBJECT_TYPE_PROJECT = "project";
//    }

    /**
     * 会员常量
     */
//    public static final class CUSTOMER_CONST {
//        /**
//         * 会员来源 -- 自主注册
//         */
//        public static final String ORIGIN_DEFAULT = "0";
//        /**
//         * 会员来源 -- 从其他系统同步
//         */
//        public static final String ORIGIN_OTHER_SYSTEM = "1";
//
//        /**
//         * 会员状态 -- 未激活
//         */
//        public static final String STATUS_INACTIVE = "0";
//        /**
//         * 会员状态 -- 正常
//         */
//        public static final String STATUS_NORMAL = "1";
//        /**
//         * 会员状态 -- 注销
//         */
//        public static final String STATUS_INVALID = "2";
//        /**
//         * 会员状态 -- 黑名单
//         */
//        public static final String STATUS_BLACKLIST = "3";
//
//        /**
//         * 积分变动类型 1-购票
//         */
//        public static final String POINT_TYPE_BUY = "1";
//
//        /**
//         * 积分变动类型 2-退款
//         */
//        public static final String POINT_TYPE_REFUND = "2";
//
//        /**
//         * 积分变动类型 3-完善个人资料
//         */
//        public static final String POINT_TYPE_REGISTER = "3";
//
//        /**
//         * 积分变动类型 9-其他
//         */
//        public static final String POINT_TYPE_OTHER = "9";
//
//        /**
//         * 积分变动人类型1-前台会员（如购票）
//         */
//        public static final String POINT_CHANGER_TYPE_CUSTOMER = "1";
//
//        /**
//         * 积分变动人类型 2-平台管理员（客服退票）
//         */
//        public static final String POINT_CHANGER_TYPE_ADMIN = "2";
//
//        /**
//         * 积分变动人类型 3-系统
//         */
//        public static final String POINT_CHANGER_TYPE_SYSTEM = "3";
//    }

    /**
     * 门户常量
     */
    public static final class PORTAL_CONST {
        /**
         * 正常
         */
        public static final String STATUS_NORMAL = "0";

        /**
         * 异常 删除
         */
        public static final String STATUS_BLOCK = "1";

        /**
         * 待审核
         */
        public static final String STATUS_CHECK = "2";

        /**
         * 审核失败
         */
        public static final String STATUS_FAIL = "3";

        /**
         * 微门户类型 -- 车站
         */
        public static final String TYPE_STATION = "0";

        /**
         * 微门户类型 -- 运输公司
         */
        public static final String TYPE_COMPANY = "1";

        /**
         * 微门户类型 -- 省集团
         */
        public static final String TYPE_GROUP_PROV = "2";

        /**
         * 微门户类型 -- 交运集团
         */
        public static final String TYPE_GROUP = "3";

        /**
         * 日志类型 -- 关注
         */
        public static final String REF_LOG_TYPE_FOCUS = "1";

        /**
         * 日志类型 -- 取消关注
         */
        public static final String REF_LOG_TYPE_UNFOCUS = "2";

        /**
         * 日志类型 -- 点击
         */
        public static final String REF_LOG_TYPE_CLICK = "3";

        /**
         * 日志类型 -- 首页列表点击
         */
        public static final String REF_LOG_TYPE_LIST_CLICK = "4";


    }

    /**
     * 订单常量
     */
//    public static final class ORDER_CONST {
//        /**
//         * 订单状态 0-初始状态
//         */
//        public static final String ORDER_STATUS_START = "0";
//
//        /**
//         * 订单状态 1-已支付未锁票
//         */
//        public static final String ORDER_STATUS_PAID_UNLOCK = "1";
//
//        /**
//         * 订单状态 2-已锁票未支付
//         */
//        public static final String ORDER_STATUS_UNPAID_LOCK = "2";
//
//        /**
//         * 订单状态 3-已支付已锁票
//         */
//        public static final String ORDER_STATUS_PAID_LOCK = "3";
//
//        /**
//         * 订单状态 4-购票成功
//         */
//        public static final String ORDER_STATUS_SUCCESS = "4";
//
//        /**
//         * 订单状态 5-无效订单
//         */
//        public static final String ORDER_STATUS_CANCEL = "5";
//        
//        /**
//         * 订单状态6-出票失败
//         * 
//         */
//        public static final String ORDER_STATUS_FAIL="6";
//        
//        /**
//         * 订单状态7-全额退款
//         * 
//         */
//        public static final String ORDER_STATUS_REFUND="7";
//
//        public static final Map<String, String> STATUS_MAP = new HashMap<>();
//        static {
//            STATUS_MAP.put(ORDER_STATUS_START, "未支付");
//            STATUS_MAP.put(ORDER_STATUS_PAID_UNLOCK, "已支付未锁票");
//            STATUS_MAP.put(ORDER_STATUS_UNPAID_LOCK, "已锁票未支付");
//            STATUS_MAP.put(ORDER_STATUS_PAID_LOCK, "已支付已锁票");
//            STATUS_MAP.put(ORDER_STATUS_SUCCESS, "购票成功");
//            STATUS_MAP.put(ORDER_STATUS_CANCEL, "无效订单");
//            STATUS_MAP.put(ORDER_STATUS_FAIL, "出票失败");
//        }
//
//        /**
//         * 是否锁票 0-未锁票 1-已锁票
//         */
//        public static final String ORDER_LOCK = "1";
//
//        /**
//         * 是否锁票 0-未锁票 1-已锁票
//         */
//        public static final String ORDER_UNLOCK = "0";
//
//        /**
//         * 状态 0-初始
//         */
//        public static final String DETAIL_STATUS_START = "0";
//
//        /**
//         * 状态 1-退票中
//         */
//        public static final String DETAIL_STATUS_REFUNDING = "1";
//
//        /**
//         * 状态 2-退票成功
//         */
//        public static final String DETAIL_STATUS_REFUND_SUCCESS = "2";
//
//        /**
//         * 状态 3-退票失败
//         */
//        public static final String DETAIL_STATUS_REFUND_FAIL = "3";
//
//        /**
//         * 状态 4-无效
//         */
//        public static final String DETAIL_STATUS_CANCEL = "4";
//
//        /**
//         * 状态 5-已改签
//         */
//        public static final String DETAIL_STATUS_CHANGE = "5";
//
//        /**
//         * 操作人类型 0:系统
//         */
//        public static final String TRACE_OPERATOR_SYSTEM = "0";
//
//        /**
//         * 操作人类型 1:会员
//         */
//        public static final String TRACE_OPERATOR_CUSTOMER = "1";
//
//        /**
//         * 操作人类型 2:管理员
//         */
//        public static final String TRACE_OPERATOR_ADMIN = "3";
//
//        /**
//         * 状态 0-初始
//         */
//        public static final String REFUND_STATUS_START = "0";
//
//        /**
//         * 状态 1-退票成功
//         */
//        public static final String REFUND_STATUS_REFUND_SUCCESS = "1";
//
//        /**
//         * 状态 2-退票失败
//         */
//        public static final String REFUND_STATUS_REFUND_FAIL = "2";
//
//        /**
//         * 状态 3-退票中
//         */
//        public static final String REFUND_STATUS_REFUNDING = "3";
//
//        /**
//         * 状态 4-补录成功
//         */
//        public static final String REFUND_STATUS_RECORD = "4";
//    }

    /**
     * 票务常量
     */
//    public static final class TICKET_CONST {
//        /**
//         * 锁票状态：0未锁票
//         */
//        public static final String TICKET_LOCK_STATUS_UNLOCK = "0";
//
//        /**
//         * 锁票状态：1锁票
//         */
//        public static final String TICKET_LOCK_STATUS_LOCK = "1";
//
//        /**
//         * 票种 -- 全票
//         */
//        public static final String TICKET_TYPE_ADULT = "0";
//
//        /**
//         * 票种 -- 全票
//         */
//        public static final String TICKET_TYPE_STUDENT = "1";
//
//        /**
//         * 票种 -- 儿童票
//         */
//        public static final String TICKET_TYPE_CHILD = "2";
//
//        /**
//         * 票种 -- 老年票
//         */
//        public static final String TICKET_TYPE_OLD = "3";
//
//        /**
//         * 票种 -- 半票
//         */
//        public static final String TICKET_TYPE_HALF = "4";
//
//        /**
//         * 票种 -- 其他
//         */
//        public static final String TICKET_TYPE_OTHER = "9";
//
//        /**
//         * 班次排序方式 -- 发车时间正序
//         */
//        public static final Integer SORT_TYPE_TIME_ASC = 1;
//        /**
//         * 班次排序方式 -- 发车时间倒序
//         */
//        public static final Integer SORT_TYPE_TIME_DESC = 2;
//        /**
//         * 班次排序方式 -- 票价正序
//         */
//        public static final Integer SORT_TYPE_PRICE_ASC = 3;
//        /**
//         * 班次排序方式 -- 票价倒序
//         */
//        public static final Integer SORT_TYPE_PRICE_DESC = 4;
//
//    }

    /**
     * 发布状态常量
     */
//    public static final class PUBLISH_STATUS_CONST {
//
//        /**
//         * 状态 -- 未发布
//         */
//        public static final String UNPUBLISHED = "0";
//        /**
//         * 状态 -- 已发布
//         */
//        public static final String PUBLISHED = "1";
//    }

    /**
     * 广告位类型常量
     */
//    public static final class ADLOCATION_TYPE_CONST {
//
//        /**
//         * 平台
//         */
//        public static final String TERRACE = "0";
//        /**
//         * TTS
//         */
//        public static final String TTS = "1";
//    }

    /**
     * 短信常量
     */
//    public static final class SMS_CONST {
//
//        /**
//         * 验证码类型 - 登录
//         */
//        public static final String VERIFY_CODE_TYPE_LOGIN = "1";
//
//        /**
//         * 验证码类型 - 注册
//         */
//        public static final String VERIFY_CODE_TYPE_REGISTER = "2";
//
//        /**
//         * 验证码类型 - 手机换绑
//         */
//        public static final String VERIFY_CODE_TYPE_REBOUND = "3";
//
//        /**
//         * 验证码类型 - 找回密码
//         */
//        public static final String VERIFY_CODE_TYPE_RECOVER = "4";
//
//        /**
//         * 验证码类型 - 订单查询
//         */
//        public static final String VERIFY_CODE_TYPE_QORDER = "5";
//
//        /**
//         * 验证码类型 - 用户激活
//         */
//        public static final String VERIFY_CODE_TYPE_ACTIVITY = "6";
//
//    }
}
