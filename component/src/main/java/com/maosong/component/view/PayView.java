package com.maosong.component.view;

/**
 * create by colin on 2019/4/17
 */
public interface PayView extends BaseView {
    /**
     * 获取订单
     */
    void getOrder();

    /**
     * 验证订单，检查是否支付成功
     * @param uniqueId 支付的唯一识别码
     */
    void verifyOrder(String uniqueId, String extra2);

    /**
     * 买商品
     * @param uniqueId 商品唯一属性
     * */
    void buySku(String uniqueId);
}
