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
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowRootMenuNoti:) name:kRootShowMenuNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowRootContentTypeNoti:) name:kShowRootContentTypeNotificationName object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveShowLatestS24:) name:kShowLatestS24VcNotificationName object:nil];
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
    [[[UIAlertView alloc] initWithTitle:bonusId message:@"1" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"aa", nil] show];
    if (!bonusId) {
        return;
    }
    [self showNewBonusVcWithId:bonusId type:QSU20NewBonusViewControllerStateAbout];
}

- (void)didReceiveHideNewBonusVcNoti:(NSNotification*)noti {
    [self hideNewBonusVc];
}

- (void)didReceiveShowNewParticipantBonusVcNoti:(NSNotification*)noti {
    NSString* bonusId = [noti.userInfo stringValueForKeyPath:@"_id"];
    [[[UIAlertView alloc] initWithTitle:bonusId message:@"2" delegate:nil cancelButtonTitle:nil otherButtonTitles:@"aa", nil] show];
    if (!bonusId) {
        return;
    }
    [self showNewBonusVcWithId:bonusId type:QSU20NewBonusViewControllerStateParticipant];
}

- (void)didReceiveHideNewParticipantBonusVcNoti:(NSNotification*)noti {
    [self hideNewBonusVc];
}


- (void)didReceiveShowBonusListVcNoti:(NSNotification*)noti {
    [self showBonusVc];
}

- (void)didReceiveShowRootMenuNoti:(NSNotification*)noti {
    [self didClickMenuBtn];
}
- (void)didReceiveShowRootContentTypeNoti:(NSNotification*)noti {
    NSDictionary* userInfo = noti.userInfo;
    NSNumber* type = [userInfo numberValueForKeyPath:@"type"];
    [self triggerToShowVc:type.unsignedIntegerValue];
}

- (void)didReceiveShowLatestS24:(NSNotification*)noti {
    [self showLatestS24Vc];
}
@end
