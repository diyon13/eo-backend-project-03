window.addEventListener('DOMContentLoaded', function () {

    const btn        = document.getElementById('checkout-btn');
    const errorBox   = document.getElementById('checkout-error');
    const errorMsg   = document.getElementById('checkout-error-msg');
    const csrfToken  = document.getElementById('csrf-token')?.value;

    const planName   = btn.dataset.planName;
    const planPrice  = parseInt(btn.dataset.planPrice);
    const userName   = btn.dataset.userName;
    const userEmail  = btn.dataset.userEmail;

    // 포트원 가맹점 식별코드
    const IMP_CODE   = 'imp80213612';

    /* 결제 버튼 클릭 */
    btn.addEventListener('click', function () {
        hideError();

        if (!planName || !planPrice) {
            return showError('플랜 정보가 올바르지 않습니다.');
        }

        IMP.init(IMP_CODE);

        IMP.request_pay({
            pg: 'kakaopay',
            pay_method: 'card',
            merchant_uid: 'order_' + new Date().getTime(),
            name: 'Prompt AI ' + planName + ' 플랜',
            amount: planPrice,
            buyer_name: userName,
            buyer_email: userEmail,
        }, async function (rsp) {
            console.log('결제 응답:', JSON.stringify(rsp));
            if (rsp.success) {
                await verifyPayment(rsp.imp_uid);
            } else {
                showError(rsp.error_msg || '결제가 취소되었습니다.');
            }
        });
    });

    /* 결제 검증 */
    async function verifyPayment(impUid) {
        btn.disabled = true;
        btn.textContent = '검증 중...';

        try {
            const res = await fetch('/payment/verify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                credentials: 'include',
                body: JSON.stringify({
                    impUid: impUid,
                    planName: planName,
                    amount: planPrice
                })
            });

            const data = await res.json();

            if (data.success) {
                alert(data.message || '결제가 완료되었습니다!');
                window.location.href = '/payment';
            } else {
                showError(data.message || '결제 검증에 실패했습니다.');
                btn.disabled = false;
                btn.textContent = '결제하기';
            }
        } catch (e) {
            showError('오류가 발생했습니다. 다시 시도해주세요.');
            btn.disabled = false;
            btn.textContent = '결제하기';
        }
    }

    /* 유틸 */
    function showError(msg) {
        errorMsg.textContent = msg;
        errorBox.style.display = 'block';
    }

    function hideError() {
        errorBox.style.display = 'none';
        errorMsg.textContent = '';
    }
});