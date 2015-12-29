//
//  QSVideoBaseViewController.h
//  qingshow-ios-native
//
//  Created by wxy325 on 3/17/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QSSingleImageScrollView.h"
#import <MediaPlayer/MediaPlayer.h>

@interface QSVideoBaseViewController : UIViewController <QSImageScrollViewBaseDelegate>

#pragma mark - IBOutlet
@property (weak, nonatomic) IBOutlet UIButton *backBtn;
@property (weak, nonatomic) IBOutlet UIView *scrollViewContainer;
@property (weak, nonatomic) IBOutlet UIView *videoContainerView;
//@property (weak, nonatomic) IBOutlet UIButton *playBtn;
@property (weak, nonatomic) IBOutlet UIButton *pauseBtn;
@property (strong, nonatomic) QSSingleImageScrollView* imageScrollView;
@property (weak, nonatomic) IBOutlet UIImageView *videoIcon;
@property (assign, nonatomic) BOOL fNeedScreenShot;

- (void)playMovie:(NSString *)path;
- (void)startVideo;
- (void)pauseVideo;
- (void)stopMovie;

- (void)updateShowImgScrollView;
- (void)playOrPauseBtnPressed:(id)sender;
- (IBAction)pauseBtnPressed:(id)sender;
- (IBAction)backBtnPressed:(id)sender;
#pragma mark - Method To Be Override
- (NSArray*)generateImagesData;
- (NSString*)generateVideoPath;

#pragma mark Mob Button Hidden
- (void)setPlayModeBtnsHidden:(BOOL)hidden;
- (void)setBtnsHiddenExceptBack:(BOOL)hidden;
#pragma mark Mob
- (void)logMobPlayVideo:(NSTimeInterval)playbackTime;
- (BOOL)isPlay;
@end
