package share.money.wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import share.money.wallet.controller.model.request.TopUpModelRequest;
import share.money.wallet.controller.model.request.WalletCreationRequestModel;
import share.money.wallet.controller.model.response.OperationStatusModel;
import share.money.wallet.controller.model.response.WalletModelResponse;
import share.money.wallet.controller.model.response.WalletRest;
import share.money.wallet.service.WalletService;
import share.money.wallet.service.dto.WalletDto;
import share.money.wallet.shared.ModelMapper;

@RestController
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping(path = "/wallet/{userId}")
    public WalletRest createWallet(@PathVariable(name = "userId") String userId, @RequestBody WalletCreationRequestModel walletCreationRequestModel) {

        WalletDto walletDto = ModelMapper.map(walletCreationRequestModel, WalletDto.class);
        walletDto.setUserId(userId);
        WalletDto userDtoOutcome = walletService.createWallet(walletDto);

        return ModelMapper.map(userDtoOutcome, WalletRest.class);
    }

    @GetMapping(value = "/wallet/{userId}")
    @ResponseBody
    public WalletModelResponse getWallet(@PathVariable(name = "userId") String userId) {

        WalletDto walletDto = walletService.findByUserId(userId);
        return ModelMapper.map(walletDto, WalletModelResponse.class);
    }

    @PutMapping(value = "/wallet/{userId}")
    @ResponseBody
    public WalletModelResponse topPopWallet(@PathVariable(name = "userId") String userId, @RequestBody TopUpModelRequest modelRequest) {

        WalletDto walletDto = walletService.topUpAccountByUserId(userId, modelRequest.getAmount());
        return ModelMapper.map(walletDto, WalletModelResponse.class);
    }

    @DeleteMapping(path = "wallet/{userId}")
    public OperationStatusModel deleteWallet(@PathVariable(name = "userId") String userId) {
        return walletService.deleteWallet(userId);
    }
}









