//
//  QSU20NewBonusViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/1.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU20NewBonusViewController.h"
#import "QSNotificationHelper.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSUnreadManager.h"

#import "QSPeopleUtil.h"
#import "QSBonusUtil.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSU20NewBonusViewController ()



@property (weak, nonatomic) IBOutlet UIImageView *itemImageView;

@property (weak, nonatomic) IBOutlet UIImageView *userHeadIconImageView;

@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *bonusNumberLabel;
@property (weak, nonatomic) IBOutlet UIView *otherUserHeadIconContainer;
@property (weak, nonatomic) IBOutlet UIButton *withdrawBtn;


@end

@implementation QSU20NewBonusViewController

#pragma mark - Init
- (instancetype)initWithBonusIndex:(NSNumber*)bonusIndex {
    self = [super initWithNibName:@"QSU20NewBonusViewController" bundle:nil];
    if (self) {
        self.bonusIndex = bonusIndex;
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
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [[QSUnreadManager getInstance] clearBonuUnread];
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)withdrawBtnPressed:(id)sender {
    [QSNotificationHelper postHideNewParticipantBonusVcNoti];
    [QSNotificationHelper postShowBonusListVcNotificationName];
}

- (IBAction)closeBtnPressed:(id)sender {
    [QSNotificationHelper postHideNewBonusVcNoti];
}

#pragma mark - 
- (void)_bindInfo {
    __weak QSU20NewBonusViewController* weakSelf = self;
    ErrorBlock errorBlock = ^(NSError *error) {
        [weakSelf showErrorHudWithError:error];
        [weakSelf performSelector:@selector(_popBack) withObject:nil afterDelay:TEXT_HUD_DELAY];
    };
    [SHARE_NW_ENGINE getLoginUserOnSucced:^(NSDictionary *peopleDict, NSDictionary *metadata) {
        NSDictionary* bonusDict = nil;
        NSArray* bonusList = [QSPeopleUtil getBonusList:peopleDict];
        if (self.bonusIndex && self.bonusIndex.intValue < bonusList.count) {
            bonusDict = bonusList[self.bonusIndex.intValue];
        } else {
            bonusDict = [QSPeopleUtil getLatestBonus:peopleDict];
        }

        NSString* itemId = [QSBonusUtil getItemRef:bonusDict];
        NSArray* participantsArray = [QSBonusUtil getParticipantsIds:bonusDict];
        
        [weakSelf.userHeadIconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict]];
        weakSelf.userNameLabel.text = [QSPeopleUtil getNickname:peopleDict];
        weakSelf.bonusNumberLabel.text = [NSString stringWithFormat:@"获得了￥%.2f的佣金", [QSBonusUtil getMoney:bonusDict].doubleValue];
        [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSDictionary *itemDict, NSDictionary *metadata) {
            [self.itemImageView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
            [self _setupOtherHeadIcons:@[peopleDict, peopleDict,peopleDict, peopleDict,peopleDict, peopleDict,peopleDict, peopleDict,peopleDict, peopleDict]];
            if (participantsArray && participantsArray.count) {
                [SHARE_NW_ENGINE queryPeoplesDetail:participantsArray onSucceed:^(NSArray *peoples) {
                    [self _setupOtherHeadIcons:peoples];
                } onError:errorBlock];
            }

        } onError:errorBlock];
    } onError:errorBlock];
    
}

- (void)_setupOtherHeadIcons:(NSArray*)peoples {
    CGSize size = self.otherUserHeadIconContainer.bounds.size;
    CGFloat spaceY = 5.f;
    CGFloat radius = (size.height - spaceY * 4) / 2;
    
    NSUInteger headNumberEveryRow = 6;
    CGFloat spaceX = size.width / headNumberEveryRow - radius;
    
    NSUInteger totalHeadNumber = peoples.count > 10 ? 10 : peoples.count;
    
    for (NSUInteger i = 0; i < totalHeadNumber; i++) {
        NSUInteger row = i / headNumberEveryRow;
        NSUInteger column = i % headNumberEveryRow;
        
        UIImageView* imgView = [[UIImageView alloc] init];
        NSDictionary* p = peoples[i];
        [imgView setImageFromURL:[QSPeopleUtil getHeadIconUrl:p]];
        CGFloat x = spaceX / 2 + column * (spaceX + radius);
        CGFloat y = spaceY / 2 + row * (spaceY + radius);
        imgView.frame = CGRectMake(x, y, radius, radius);
        imgView.layer.cornerRadius = radius / 2;
        imgView.layer.masksToBounds = YES;
        imgView.layer.borderColor = [UIColor whiteColor].CGColor;
        imgView.layer.borderWidth = 1.f;
        [self.otherUserHeadIconContainer addSubview:imgView];
    }
}
- (void)_popBack {
    [self closeBtnPressed:nil];
}
@end
