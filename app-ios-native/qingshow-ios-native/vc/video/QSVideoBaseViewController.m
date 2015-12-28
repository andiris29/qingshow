//
//  QSVideoBaseViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 3/17/15.
//  Copyright (c) 2015 QS. All rights reserved.
//


#import "QSVideoBaseViewController.h"
#import "UIViewController+QSExtension.h"

@interface QSVideoBaseViewController ()

@property (strong, nonatomic) UIImage* videoScreenShotImage;

@property (strong, nonatomic) MPMoviePlayerController* movieController;

@end

@implementation QSVideoBaseViewController

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    self.fNeedScreenShot = YES;
    // Do any additional setup after loading the view.
    if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    self.imageScrollView = [[QSSingleImageScrollView alloc] initWithFrame:self.scrollViewContainer.bounds];
    self.imageScrollView.pageControlOffsetY = 10.f;
    self.imageScrollView.pageControl.hidden = YES;
    self.imageScrollView.delegate = self;
    [self.scrollViewContainer addSubview:self.imageScrollView];

    [self hideNaviBackBtnTitle];

}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController setNavigationBarHidden:YES animated:YES];
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    self.imageScrollView.frame = self.scrollViewContainer.bounds;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self stopMovie];
}

#pragma mark - Movie
#pragma mark Init MovieController
-(void)playMovie:(NSString *)path{
    if (!self.movieController) {
        NSURL *url = [NSURL URLWithString:path];
        self.movieController = [[MPMoviePlayerController alloc] initWithContentURL:url];
        self.movieController.view.frame = self.videoContainerView.frame;
        self.movieController.scalingMode = MPMovieScalingModeAspectFill;
        self.movieController.controlStyle = MPMovieControlStyleNone;
        [self.videoContainerView addSubview:self.movieController.view];
        self.movieController.view.userInteractionEnabled = NO;
        
        //Notification
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(myMovieFinishedCallback:)
                                                     name:MPMoviePlayerPlaybackDidFinishNotification
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleFirstShowHideVideo)
                                                     name:MPMoviePlayerPlaybackStateDidChangeNotification
                                                   object:nil];
    }
    [self startVideo];
}
#pragma mark - Movie
#pragma mark Basic Control Method
- (void)startVideo
{
    self.movieController.view.hidden = NO;
    [self.movieController play];
    [self setPlayModeBtnsHidden:YES];
    self.videoContainerView.userInteractionEnabled = YES;
    self.videoIcon.hidden = YES;
    self.pauseBtn.hidden = NO;
}
- (void)pauseVideo
{
    //self.movieController.view will be hidden after get thumbnail
    self.videoContainerView.userInteractionEnabled = NO;
    self.videoScreenShotImage = nil;
    [self.movieController pause];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveThunbnailImage:) name:MPMoviePlayerThumbnailImageRequestDidFinishNotification object:nil];
    [self.movieController requestThumbnailImagesAtTimes:@[@(self.movieController.currentPlaybackTime)] timeOption:MPMovieTimeOptionExact];
    [self setPlayModeBtnsHidden:NO];
    self.videoIcon.hidden = NO;
    self.pauseBtn.hidden = YES;
}


- (void)stopMovie
{
    if (self.movieController) {
        self.videoScreenShotImage = nil;
        self.movieController.view.hidden = YES;
        [self.movieController stop];
        [self setPlayModeBtnsHidden:NO];
        self.videoContainerView.userInteractionEnabled = NO;
        self.movieController.initialPlaybackTime = 0;
        [self updateShowImgScrollView];
        
    }
    self.videoIcon.hidden = NO;
    self.pauseBtn.hidden = YES;
}

#pragma mark - Notification
- (void)handleFirstShowHideVideo
{
    if (self.movieController.playbackState == MPMoviePlaybackStatePaused || self.movieController.playbackState == MPMoviePlaybackStateStopped) {
        [self setPlayModeBtnsHidden:NO];
    } else {
        [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerPlaybackStateDidChangeNotification object:nil];
        [self setPlayModeBtnsHidden:YES];
        self.videoContainerView.userInteractionEnabled = YES;
    }
}

-(void)myMovieFinishedCallback:(NSNotification*)notify
{
    [self stopMovie];
}


#pragma mark Thunbnail
- (void)didReceiveThunbnailImage:(NSNotification*)note
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerThumbnailImageRequestDidFinishNotification object:nil];
    NSDictionary * userInfo = [note userInfo];
    self.videoScreenShotImage = (UIImage *)[userInfo objectForKey:MPMoviePlayerThumbnailImageKey];
    [self updateShowImgScrollView];
    
    self.movieController.view.hidden = YES;
}

- (void)updateShowImgScrollView
{
    NSMutableArray* array = [@[] mutableCopy];
    if (self.videoScreenShotImage && self.fNeedScreenShot) {
        [array addObject:self.videoScreenShotImage];
    }
    NSArray* previewArray =  [self generateImagesData];;
    [array addObjectsFromArray:previewArray];
    self.imageScrollView.imageUrlArray = array;
    [self.imageScrollView scrollToPage:0];
    [self setBtnsHiddenExceptBack:NO];
}

#pragma mark - QSImageScrollViewBaseDelegate
- (void)imageScrollView:(QSImageScrollViewBase*)view didChangeToPage:(int)page
{
    if (self.videoScreenShotImage && page != 0) {
        [self setBtnsHiddenExceptBack:YES];
    } else {
        [self setBtnsHiddenExceptBack:NO];
    }
}


#pragma mark - Method To Be Override
#pragma mark - Generate Data
- (NSArray*)generateImagesData
{
    return nil;
}
- (NSString*)generateVideoPath
{
    return nil;
}

#pragma mark Btn
- (void)setPlayModeBtnsHidden:(BOOL)hidden
{
}
- (void)setBtnsHiddenExceptBack:(BOOL)hidden
{
}
#pragma mark Mob
- (void)logMobPlayVideo:(NSTimeInterval)playbackTime
{
}


#pragma mark - IBAction
- (BOOL)isPlay {
    if (![self generateVideoPath]) {
        return NO;
    } else {
        return !(!self.movieController || self.movieController.playbackState == MPMoviePlaybackStatePaused || self.movieController.playbackState == MPMoviePlaybackStateStopped);
    }
}
- (IBAction)playOrPauseBtnPressed:(id)sender {
    NSString* video = [self generateVideoPath];
    if (video) {
        if (![self isPlay]) {
            [self playMovie:video];
        } else {
            [self pauseVideo];
        }
    }
}

- (IBAction)backBtnPressed:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromLeft;
    tran.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseOut];
    [self.navigationController.view.layer addAnimation:tran forKey:@"transition_to_root"];
    if (self.navigationController) {
        [self.navigationController popViewControllerAnimated:NO];
    } else {
        [self dismissViewControllerAnimated:NO completion:nil];
    }
    
    if (self.movieController) {
        [self logMobPlayVideo:self.movieController.currentPlaybackTime];

    }
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
