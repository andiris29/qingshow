//
//  QSU21NewParticipantBonusViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/1.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU21NewParticipantBonusViewController.h"
#import "QSNotificationHelper.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"

#import "QSPeopleUtil.h"
#import "QSBonusUtil.h"
#import "QSTradeUtil.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"


@interface QSU21NewParticipantBonusViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *itemImageView;
@property (weak, nonatomic) IBOutlet UIImageView *userHeadIconImageView;
@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *peopleDescLabel;

@property (weak, nonatomic) IBOutlet UILabel *priceLabel;
@property (weak, nonatomic) IBOutlet UIButton *withdrawBtn;

@end

@implementation QSU21NewParticipantBonusViewController

#pragma mark - Init
- (instancetype)init {
    self = [super initWithNibName:@"QSU21NewParticipantBonusViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self _configUi];
    [self _bindInfo];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)_configUi {
    self.itemImageView.layer.cornerRadius = 10.f;
    self.itemImageView.layer.masksToBounds = YES;
    
    self.userHeadIconImageView.layer.cornerRadius = self.userHeadIconImageView.bounds.size.height / 2;
    self.userHeadIconImageView.layer.masksToBounds = YES;
    self.userHeadIconImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    self.userHeadIconImageView.layer.borderWidth = 3.f;
    
    self.withdrawBtn.layer.cornerRadius = self.withdrawBtn.bounds.size.height / 2;
    self.withdrawBtn.layer.masksToBounds = YES;
}

#pragma mark - IBAction
- (IBAction)closeBtnPressed:(id)sender {
    [QSNotificationHelper postHideNewParticipantBonusVcNoti];
}
- (IBAction)withdrawBtnPressed:(id)sender {
    [QSNotificationHelper postHideNewParticipantBonusVcNoti];
    [QSNotificationHelper postShowBonusListVcNotificationName];
}

#pragma mark -
- (void)_bindInfo {
    __weak QSU21NewParticipantBonusViewController* weakSelf = self;
    ErrorBlock errorBlock = ^(NSError *error) {
        [weakSelf showErrorHudWithError:error];
        [weakSelf performSelector:@selector(_popBack) withObject:nil afterDelay:TEXT_HUD_DELAY];
    };
    [SHARE_NW_ENGINE getLoginUserOnSucced:^(NSDictionary *peopleDict, NSDictionary *metadata) {
        NSDictionary* bonusDict = [QSPeopleUtil getLatestBonus:peopleDict];
        NSString* itemId = [QSBonusUtil getItemRef:bonusDict];
        NSString* tradeId = [QSBonusUtil getTradeRef:bonusDict];
        

        weakSelf.priceLabel.text = [NSString stringWithFormat:@"￥%.2f", [QSBonusUtil getMoney:bonusDict].doubleValue];
        [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSDictionary *itemDict, NSDictionary *metadata) {
            [weakSelf.itemImageView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
            [SHARE_NW_ENGINE queryTradeDetail:tradeId onSucceed:^(NSDictionary *tradeDict) {
                NSString* promoRef = [QSTradeUtil getPromoterId:tradeDict];
                [SHARE_NW_ENGINE queryPeopleDetail:promoRef onSucceed:^(NSDictionary *peopleDict) {
                    [weakSelf.userHeadIconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict]];
                    weakSelf.userNameLabel.text = [QSPeopleUtil getNickname:peopleDict];
                    weakSelf.peopleDescLabel.text = [NSString stringWithFormat:@"您获得了来自%@的共享佣金", [QSPeopleUtil getNickname:peopleDict]];
                } onError:errorBlock];
                
            } onError:errorBlock];
        } onError:errorBlock];
    } onError:errorBlock];
}

- (void)_popBack {
    [self closeBtnPressed:nil];
}

@end
