package hotel_kiosk.service.customer;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.exception.SolapiEmptyResponseException;
import com.solapi.sdk.message.exception.SolapiMessageNotReceivedException;
import com.solapi.sdk.message.exception.SolapiUnknownException;
import com.solapi.sdk.message.model.Message;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class SmsService {

    @Value("${sms.api.key}")
    private String apiKey;

    @Value("${sms.api.secret}")
    private String apiSecret;

    @Value("${sms.api.phone}")
    private String fromPhone;

    public boolean sendSms(String toPhone, String content) {
        DefaultMessageService messageService =
                SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);

        Message message = new Message();
        message.setFrom(fromPhone);
        message.setTo(toPhone);
        message.setText(content);

        try {
            messageService.send(message);
            log.info("SMS 발송 성공 - to: {}", toPhone);
            return true;
        } catch (SolapiMessageNotReceivedException e) {
            log.error("SMS 발송 실패 - failedMessageList: {}, message: {}", e.getFailedMessageList(), e.getMessage());
            return false;
        } catch (SolapiEmptyResponseException | SolapiUnknownException e) {
            log.error("SMS 발송 오류 - {}", e.getMessage());
            return false;
        }
    }
}
