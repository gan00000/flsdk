//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gamamobi.onestore.api;

public enum IapResult {
    RESULT_OK(0, "성공"),
    RESULT_USER_CANCELED(1, "결제가 취소되었습니다."),
    RESULT_SERVICE_UNAVAILABLE(2, "구매에 실패했습니다. (단말 또는 서버 네트워크 오류가 발생하였습니다)"),
    RESULT_BILLING_UNAVAILABLE(3, "구매에 실패했습니다. (구매 처리 과정에서 오류가 발생하였습니다)"),
    RESULT_ITEM_UNAVAILABLE(4, "구매에 실패했습니다. (상품이 판매중이 아니거나 구매할 수 없는 상태입니다)"),
    RESULT_DEVELOPER_ERROR(5, "구매에 실패했습니다. (올바르지 않은 구매 요청입니다)"),
    RESULT_ERROR(6, "구매에 실패했습니다. (정의되지 않은 기타 오류가 발생했습니다)"),
    RESULT_ITEM_ALREADY_OWNED(7, "구매에 실패했습니다. (이미 아이템을 소유하고 있습니다)"),
    RESULT_ITEM_NOT_OWNED(8, "구매에 실패했습니다. (아이템을 소유하고 있지 않아 comsume 할 수 없습니다)"),
    RESULT_FAIL(9, "결제에 실패했습니다. 결제 가능 여부 및 결제 수단 확인 후 다시 결제해주세요."),
    RESULT_NEED_LOGIN(10, "구매에 실패했습니다. (구매를 위해 원스토어 로그인이 필요합니다)"),
    RESULT_NEED_UPDATE(11, "구매에 실패했습니다. (원스토어 서비스앱의 업데이트가 필요합니다)"),
    RESULT_SECURITY_ERROR(12, "구매에 실패했습니다. (비정상 앱에서 결제가 요청되었습니다)"),
    IAP_ERROR_DATA_PARSING(1001, "응답 데이터 파싱 오류"),
    IAP_ERROR_SIGNATURE_VERIFICATION(1002, "구매정보의 서명 검증 에러"),
    IAP_ERROR_ILLEGAL_ARGUMENT(1003, "정상적이지 않는 파라미터 입력"),
    IAP_ERROR_UNDEFINED_CODE(1004, "정의되지 않는 에러"),
    IAP_ERROR_SIGNATURE_NOT_VALIDATION(1005, "입력하신 라이센스 키가 유효하지 않습니다");

    private int code;
    private String description;

    private IapResult(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public static IapResult getResult(int code) {
        IapResult[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            IapResult item = var1[var3];
            if (item.getCode() == code) {
                return item;
            }
        }

        return IAP_ERROR_UNDEFINED_CODE;
    }

    public boolean isSuccess() {
        return RESULT_OK == this;
    }

    public boolean isFailed() {
        return !this.isSuccess();
    }

    public boolean equalCode(int code) {
        return this.code == code;
    }

    public String toString() {
        return "IapResult: " + this.getCode() + " " + this.getDescription();
    }
}
