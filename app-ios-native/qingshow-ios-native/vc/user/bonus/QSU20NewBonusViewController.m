//
//  QSU20NewBonusViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/11/1.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSU20NewBonusViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+ShowHud.h"
#import "QSUnreadManager.h"
#import "QSRootNotificationHelper.h"
#import "QSPeopleUtil.h"
#import "QSBonusUtil.h"
#import "QSItemUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "NSArray+QSExtension.h"
#import "QSUserManager.h"

#define HEAD_NUMBER_EVERY_ROW 10
#define HEAD_ROW_NUMBER 2

@interface QSU20NewBonusViewController ()
@property (strong, nonatomic) NSDictionary* bonusDict;
#pragma mark - IBOutlet
@property (weak, nonatomic) IBOutlet UIImageView *itemImageView;

@property (weak, nonatomic) IBOutlet UIImageView *userHeadIconImageView;

@property (weak, nonatomic) IBOutlet UILabel *userNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *bonusNumberLabel;
@property (weak, nonatomic) IBOutlet UIButton *withdrawBtn;

//Bottom
@property (weak, nonatomic) IBOutlet UILabel *bottomTitleLabel;

//Other Participant
@property (weak, nonatomic) IBOutlet UIView *otherUserHeadIconContainer;
@property (weak, nonatomic) IBOutlet UIButton *aboutParticipantBonusBtn;

//About
@property (weak, nonatomic) IBOutlet UILabel *aboutLabel;

@property (assign, nonatomic) QSU20NewBonusViewControllerState state;
@property (assign, nonatomic) BOOL fShouldTapToShowParticipant;
@end

@implementation QSU20NewBonusViewController

#pragma mark - Init
- (instancetype)initWithBonus:(NSDictionary*)bonusDict state:(QSU20NewBonusViewControllerState)state {
    self = [super initWithNibName:@"QSU20NewBonusViewController" bundle:nil];
    if (self) {
        self.bonusDict = bonusDict;
        _state = state;
        self.fShouldTapToShowParticipant = state == QSU20NewBonusViewControllerStateParticipant;
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)withdrawBtnPressed:(id)sender {
    [QSRootNotificationHelper postHideNewParticipantBonusVcNoti];
    [QSRootNotificationHelper postShowBonusListVcNotificationName];
}

- (IBAction)closeBtnPressed:(id)sender {
    [QSRootNotificationHelper postHideNewBonusVcNoti];
}

- (IBAction)aboutParticipantBonusBtnPressed:(id)sender {
    self.state = QSU20NewBonusViewControllerStateAbout;
}

#pragma mark - Gesture
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    if (self.fShouldTapToShowParticipant) {
        self.state = QSU20NewBonusViewControllerStateParticipant;
    }

}

#pragma mark - Private
- (void)_configUi {
    self.itemImageView.layer.cornerRadius = 10.f;
    self.itemImageView.layer.masksToBounds = YES;
    self.itemImageView.layer.borderColor = [UIColor colorWithRed:210.f/255.f green:210.f/255.f blue:210.f/255.f alpha:1.f].CGColor;
    self.itemImageView.layer.borderWidth = 1.f;
    
    self.userHeadIconImageView.layer.cornerRadius = self.userHeadIconImageView.bounds.size.height / 2;
    self.userHeadIconImageView.layer.masksToBounds = YES;
    self.userHeadIconImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    self.userHeadIconImageView.layer.borderWidth = 3.f;
    
    self.withdrawBtn.layer.cornerRadius = self.withdrawBtn.bounds.size.height / 2;
    self.withdrawBtn.layer.masksToBounds = YES;
    
    self.aboutParticipantBonusBtn.layer.cornerRadius = self.aboutParticipantBonusBtn.bounds.size.height / 2;
    self.aboutParticipantBonusBtn.layer.masksToBounds = YES;
    self.aboutParticipantBonusBtn.layer.borderColor = [UIColor blackColor].CGColor;
    self.aboutParticipantBonusBtn.layer.borderWidth = 1.f;
    
    [self _updateUiForState];
}

- (void)_bindInfo {
    __weak QSU20NewBonusViewController* weakSelf = self;
    ErrorBlock errorBlock = ^(NSError *error) {
        [weakSelf showErrorHudWithError:error];
        [weakSelf performSelector:@selector(_popBack) withObject:nil afterDelay:TEXT_HUD_DELAY];
    };
    NSDictionary* peopleDict = [QSUserManager shareUserManager].userInfo;
    
    NSDictionary* bonusDict = self.bonusDict;
    
    NSString* itemId = [QSBonusUtil getTradeItemId:bonusDict];
    NSArray* participantsArray = [QSBonusUtil getParticipantsIds:bonusDict];
    
    [weakSelf.userHeadIconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict]];
    weakSelf.userNameLabel.text = [QSPeopleUtil getNickname:peopleDict];
    NSMutableAttributedString* bonusText = [[NSMutableAttributedString alloc] initWithString:@"获得了"];
    NSString* bonusNumberText = [NSString stringWithFormat:@"￥%.2f ", [QSBonusUtil getMoney:bonusDict].doubleValue];
    NSUInteger begin = bonusText.length;
    NSUInteger length = bonusNumberText.length;
    NSRange range = NSMakeRange(begin, length);
    [bonusText appendAttributedString:[[NSAttributedString alloc] initWithString:bonusNumberText]];
    [bonusText appendAttributedString:[[NSAttributedString alloc] initWithString:@"的佣金"]];
    
    UIColor* colorPink = [UIColor colorWithRed:248.f/255.f green:62.f/255.f blue:91.f/255.f alpha:1.f];
    [bonusText addAttribute:NSUnderlineStyleAttributeName value:@(NSUnderlinePatternSolid | NSUnderlineStyleSingle) range:range];
    [bonusText addAttribute:NSUnderlineColorAttributeName value:colorPink range:range];
    [bonusText addAttribute:NSForegroundColorAttributeName value:colorPink range:range];
    [bonusText addAttribute:NSFontAttributeName value:NEWFONT range:range];
    
    
    weakSelf.bonusNumberLabel.attributedText = bonusText;
    [SHARE_NW_ENGINE getItemWithId:itemId onSucceed:^(NSDictionary *itemDict, NSDictionary *metadata) {
        [self.itemImageView setImageFromURL:[QSItemUtil getThumbnail:itemDict]];
        if (participantsArray && participantsArray.count) {
            NSArray* a = [participantsArray mapUsingBlock:^id(NSDictionary* p) {
                return [QSEntityUtil getIdOrEmptyStr:p];
            }];
            [SHARE_NW_ENGINE queryPeoplesDetail:a onSucceed:^(NSArray *peoples) {
                [self _setupOtherHeadIcons:peoples];
            } onError:errorBlock];
        }
        
    } onError:errorBlock];
    
}

- (void)_setupOtherHeadIcons:(NSArray*)peoples {
    CGSize size = self.otherUserHeadIconContainer.bounds.size;
    NSUInteger headNumberEveryRow = HEAD_NUMBER_EVERY_ROW;

    CGFloat spaceX = 3.f;
    CGFloat radius = (size.width - spaceX * (headNumberEveryRow + 1)) / headNumberEveryRow;
    CGFloat spaceY = (size.height - (HEAD_ROW_NUMBER * radius)) / (HEAD_ROW_NUMBER + 1);
    
    NSUInteger maxHeadNumber = HEAD_NUMBER_EVERY_ROW * HEAD_ROW_NUMBER;
    NSUInteger totalHeadNumber = peoples.count > maxHeadNumber ? maxHeadNumber : peoples.count;
    
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

#pragma mark - Getter and Setter
- (void)setState:(QSU20NewBonusViewControllerState)state {
    _state = state;
    [self _updateUiForState];
}
- (void)_updateUiForState {
    switch (_state) {
        case QSU20NewBonusViewControllerStateParticipant: {
            self.bottomTitleLabel.text = @"获得共享佣金的账户";
            self.aboutParticipantBonusBtn.hidden = NO;
            self.otherUserHeadIconContainer.hidden = NO;
            self.aboutLabel.hidden = YES;
            break;
        }
        case QSU20NewBonusViewControllerStateAbout: {
            self.bottomTitleLabel.text = @"什么是共享佣金";
            self.aboutParticipantBonusBtn.hidden = YES;
            self.otherUserHeadIconContainer.hidden = YES;
            self.aboutLabel.hidden = NO;
            break;
        }
        default:
            break;
    }
}

@end
