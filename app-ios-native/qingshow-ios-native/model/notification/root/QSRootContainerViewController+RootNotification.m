//
//  QSRootContainerViewController+RootNotification.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/2.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSRootContainerViewController+RootNotification.h"
#import "NSDictionary+QSExtension.h"


@implementation QSRootContainerViewController(RootNotification)

- (void)observeNotifications {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceivePrompToLoginNotification:) name:kShowLoginPrompVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHidePrompToLoginNotification:) name:kHideLoginPrompVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveScheduleToShowLoginGuideNotification:) name:kScheduleToShowLoginGuideNotificationName object:nil];
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowNewBonusVcNoti:) name:kShowNewBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHideNewBonusVcNoti:) name:kHideNewBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowNewParticipantBonusVcNoti:) name:kShowNewParticipantBonusVcNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveHideNewParticipantBonusVcNoti:) name:kHideNewParticipantBonusVcNotificationName object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowBonusListVcNoti:) name:kShowBonusListVcNotificatinName object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowS01VcWithSegmentIndex:) name:kShowS01VcWithSegmentIndexNotificationName object:nil];
}

#pragma mark - Notification
- (void)didReceivePrompToLoginNotification:(NSNotification*)noti {
    [self showRegisterVc];
}
- (void)didReceiveHidePrompToLoginNotification:(NSNotification*)noti {
    [self hideRegisterVc];
}

- (void)didReceiveScheduleToShowLoginGuideNotification:(NSNotification*)noti {
    [self scheduleToShowLoginGuide];
}


- (void)didReceiveShowNewBonusVcNoti:(NSNotification*)noti {
    NSString* bonusId = [noti.userInfo stringValueForKeyPath:@"_id"];
    if (!bonusId) {
        return;
    }
    [self showNewBonusVcWithId:bonusId type:QSU20NewBonusViewControllerStateParticipant];
}

- (void)didReceiveHideNewBonusVcNoti:(NSNotification*)noti {
    [self hideNewBonusVc];
}

- (void)didReceiveShowNewParticipantBonusVcNoti:(NSNotification*)noti {
    NSString* bonusId = [noti.userInfo stringValueForKeyPath:@"_id"];
    if (!bonusId) {
        return;
    }
    [self showNewBonusVcWithId:bonusId type:QSU20NewBonusViewControllerStateAbout];
}

- (void)didReceiveHideNewParticipantBonusVcNoti:(NSNotification*)noti {
    [self hideNewBonusVc];
}


- (void)didReceiveShowBonusListVcNoti:(NSNotification*)noti {
#warning TODO Show Bonus Bc
    //    UIViewController* vc = [self triggerToShowVc:QSRootMenuItemSetting];
    //    if ([vc isKindOfClass:[QSU02UserSettingViewController class]]) {
    //        QSU02UserSettingViewController* u02Vc = (QSU02UserSettingViewController*)vc;
    //        [u02Vc showBonuesVC];
    //    }
}




- (void)didReceiveShowS01VcWithSegmentIndex:(NSNotification*)noti {
    NSDictionary* userInfo = noti.userInfo;
    NSNumber* index = [userInfo numberValueForKeyPath:@"index"];
    [self showS01WithIndex:index.intValue];
}

@end
