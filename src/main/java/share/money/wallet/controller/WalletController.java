package share.money.wallet.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import share.money.wallet.controller.model.request.TopUpModelRequest;
import share.money.wallet.controller.model.request.WalletCreationRequestModel;
import share.money.wallet.controller.model.response.WalletModelResponse;
import share.money.wallet.controller.model.response.WalletRest;
import share.money.wallet.service.WalletService;
import share.money.wallet.service.dto.WalletDto;

@RestController
public class WalletController {

    @Autowired
    private WalletService walletService;


    @PostMapping(path = "/wallet/{userId}")
    public WalletRest createUser(@PathVariable(name = "userId") String userId, @RequestBody WalletCreationRequestModel walletCreationRequestModel) {

        WalletDto walletDto = new ModelMapper().map(walletCreationRequestModel, WalletDto.class);
        walletDto.setUserId(userId);
        WalletDto userDtoOutcome = walletService.createWallet(walletDto);

        return new ModelMapper().map(userDtoOutcome, WalletRest.class);
    }

    @RequestMapping(value = "/wallet/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public WalletModelResponse getUserMoneyTotalByUserId(@PathVariable(name = "userId") String userId) {

        WalletDto walletDto = walletService.findByUserId(userId);
        return new ModelMapper().map(walletDto, WalletModelResponse.class);
    }

    @RequestMapping(value = "/wallet/{userId}", method = RequestMethod.PUT)
    @ResponseBody
    public WalletModelResponse topUpAccount(@PathVariable(name = "userId") String userId, @RequestBody TopUpModelRequest modelRequest) {

        WalletDto walletDto = walletService.topUpAccountByUserId(userId, modelRequest.getAmount());
        return new ModelMapper().map(walletDto, WalletModelResponse.class);
    }
}

