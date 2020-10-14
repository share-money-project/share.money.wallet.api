package share.money.wallet.ampq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import share.money.wallet.config.SpringRabbitConfig;
import share.money.wallet.externalservice.dto.TicketCreatedDto;
import share.money.wallet.externalservice.model.TicketRequestDto;
import share.money.wallet.service.WalletService;
import share.money.wallet.service.dto.WalletDto;
import share.money.wallet.shared.ModelMapper;

@Service
public class MessageListener {

    private static final String WALLET_CREATE = SpringRabbitConfig.QUEUE_WALLET_CREATE;
    private static final String TICKET_CREATE = SpringRabbitConfig.QUEUE_TICKET_CREATE;

    @Autowired
    private WalletService walletService;

    @RabbitListener(queues = WALLET_CREATE)
    public void receiveMessage(String message) {
        walletService.createWallet(new WalletDto(message, 0.0));
    }

    @RabbitListener(queues = TICKET_CREATE)
    public void receiveMessageApplicationCreated(TicketRequestDto ticketRequestDto) {
        TicketCreatedDto createdDto = ModelMapper.map(ticketRequestDto, TicketCreatedDto.class);
        walletService.checkTicketCreated(createdDto);
    }
}
