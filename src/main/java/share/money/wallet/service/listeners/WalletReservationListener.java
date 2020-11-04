package share.money.wallet.service.listeners;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import share.money.commons.events.WalletReservationRequestEvent;
import share.money.commons.events.WalletReservationResponseEvent;
import share.money.wallet.config.JmsConstants;
import share.money.wallet.service.BusinessException;
import share.money.wallet.service.WalletService;
import share.money.wallet.service.dto.WalletDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class WalletReservationListener {

    private final WalletService walletService;
    private final AmqpTemplate amqpTemplate;

    @RabbitListener(queues = JmsConstants.WALLET_RESERVATION_REQUEST)
    public void listenForUserValidationResponse(WalletReservationRequestEvent requestEvent) {

        try {
            final WalletDto walletDto = walletService.findByUserId(requestEvent.getOfferDto().getUserId());
            if (walletDto.getTotal() >= requestEvent.getOfferDto().getAmount()) {
                amqpTemplate.convertAndSend(JmsConstants.WALLET_EXCHANGE, JmsConstants.WALLET_RESERVE_RESPONSE_KEY,
                        WalletReservationResponseEvent.builder().offerDto(requestEvent.getOfferDto()).reservationErrorExist(false).build());
                log.debug(String.format("Wallet reservation for offerId  [%s] is [%s]", requestEvent.getOfferDto().getOfferId(), false));
            } else {
                amqpTemplate.convertAndSend(JmsConstants.WALLET_EXCHANGE, JmsConstants.WALLET_RESERVE_RESPONSE_KEY,
                        WalletReservationResponseEvent.builder().offerDto(requestEvent.getOfferDto()).reservationErrorExist(true).reservationErrorReason("Not enough money").build());
            }
        } catch (BusinessException e) {
            amqpTemplate.convertAndSend(JmsConstants.WALLET_EXCHANGE, JmsConstants.WALLET_RESERVE_RESPONSE_KEY,
                    WalletReservationResponseEvent.builder().offerDto(requestEvent.getOfferDto()).reservationErrorExist(true).reservationErrorReason(e.getMessage()).build());
            log.debug(String.format("Wallet reservation for offerId  [%s] is [%s]", requestEvent.getOfferDto().getOfferId(), true));
        }
    }
}
