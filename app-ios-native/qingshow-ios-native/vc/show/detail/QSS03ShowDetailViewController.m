//
//  QSS03ShowDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ShowDetailViewController.h"
#import "QSSingleImageScrollView.h"
#import "QSP02ModelDetailViewController.h"
#import "QSS03ItemDetailViewController.h"
#import "QSS04CommentListViewController.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import <MediaPlayer/MediaPlayer.h>
#import "QSNetworkKit.h"
#import "QSItemUtil.h"

#import "UIViewController+ShowHud.h"
#import <QuartzCore/QuartzCore.h>
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"
#import "UIView+ScreenShot.h"

#define PAGE_ID @"S03"

@interface QSS03ShowDetailViewController ()

@property (strong, nonatomic) QSSingleImageScrollView* showImageScrollView;
//@property (strong, nonatomic) QSItemImageScrollView* itemImageScrollView;

@property (strong, nonatomic) NSDictionary* showDict;
@property (strong, nonatomic) MPMoviePlayerController* movieController;


@property (assign, nonatomic) CGRect commentBtnRect;
@property (assign, nonatomic) CGRect shareBtnRect;
@property (assign, nonatomic) CGRect playBtnRect;

@property (strong, nonatomic) UIImage* videoScreenShotImage;

@property (strong, nonatomic) QSShareViewController* shareVc;
@end

@implementation QSS03ShowDetailViewController
#pragma mark - Init Method
- (id)initWithShow:(NSDictionary*)showDict
{
    self = [self initWithNibName:@"QSS03ShowDetailViewController" bundle:nil];
    if (self) {
        self.showDict = showDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {

    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
//    self.modelContainer.layer.cornerRadius = 4;
//    self.modelContainer.layer.masksToBounds = YES;
//    self.itemContainer.layer.cornerRadius = 4;
//    self.itemContainer.layer.masksToBounds = YES;
//    self.showContainer.layer.cornerRadius = 4;
//    self.showContainer.layer.masksToBounds = YES;

    self.showContainer.frame = [UIScreen mainScreen].bounds;
    self.showImageScrollView = [[QSSingleImageScrollView alloc] initWithFrame:self.showContainer.frame];
    self.showImageScrollView.pageControlOffsetY = 10.f;
    self.showImageScrollView.pageControl.hidden = YES;
    self.showImageScrollView.delegate = self;
    [self.showContainer addSubview:self.showImageScrollView];

    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    
    self.headIconImageView.layer.cornerRadius = self.headIconImageView.frame.size.width / 2;
    self.headIconImageView.layer.masksToBounds = YES;
    self.headIconImageView.layer.borderWidth = 1;
    self.headIconImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    
    [self bindWithDict:self.showDict];
    
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    

    
    self.commentBtnRect = self.commentBtn.frame;
    self.shareBtnRect = self.shareBtn.frame;
    self.playBtnRect = self.playBtn.frame;
    
    self.shareVc = [[QSShareViewController alloc] init];
    self.shareVc.delegate = self;
    [self.view addSubview:self.shareVc.view];
    self.shareVc.view.frame = self.view.bounds;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    [self bindExceptImageWithDict:self.showDict];
    [MobClick beginLogPageView:PAGE_ID];
}
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    if (!self.itemListVc) {
        __weak QSS03ShowDetailViewController* weakSelf = self;
        if (self.showDict) {
            [weakSelf bindWithDict:self.showDict];
        }
        [SHARE_NW_ENGINE queryShowDetail:self.showDict onSucceed:^(NSDictionary * dict) {
            weakSelf.showDict = dict;
            [weakSelf bindWithDict:dict];
        } onError:^(NSError *error) {
            
        }];
    }

}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideSharePanel];
    [MobClick endLogPageView:PAGE_ID];
}


- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self stopMovie];
    
}

- (void)bindWithDict:(NSDictionary*)dict
{
    [self bindExceptImageWithDict:dict];
    //Image
    //    self.showImageScrollView.imageUrlArray = previewArray;
    [self updateShowImgScrollView];
}

- (void)bindExceptImageWithDict:(NSDictionary*)dict
{
    //Model
    NSDictionary* peopleInfo = [QSShowUtil getPeopleFromShow:dict];
    
    NSURL* iconUrl = [QSPeopleUtil getHeadIconUrl:peopleInfo];
    [self.headIconImageView setImageFromURL:iconUrl];
    
    self.nameLabel.text = [QSPeopleUtil getName:peopleInfo];
    self.detailLabel.text = [QSPeopleUtil getDetailDesc:peopleInfo];
    self.contentLabel.text = [QSPeopleUtil getStatus:peopleInfo];
    
    //Like Btn
    [self setLikeBtnHover:[QSShowUtil getIsLike:dict]];
    
    [self.commentBtn setTitle:[QSShowUtil getNumberCommentsDescription:dict] forState:UIControlStateNormal];
    [self.favorBtn setTitle:[QSShowUtil getNumberLikeDescription:dict] forState:UIControlStateNormal];
    [self.itemBtn setTitle:[QSShowUtil getNumberItemDescription:self.showDict] forState:UIControlStateNormal];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - UI
- (void)setLikeBtnHover:(BOOL)fHover
{
    if (fHover) {
        [self.favorBtn setBackgroundImage:[UIImage imageNamed:@"s03_like_btn_hover"] forState:UIControlStateNormal];
    } else {
        [self.favorBtn setBackgroundImage:[UIImage imageNamed:@"s03_like_btn"] forState:UIControlStateNormal];
    }

}


#pragma mark - IBAction
- (IBAction)playBtnPressed:(id)sender {
    [self hideSharePanel];
    NSString* video = self.showDict[@"video"];
    if (video) {
        [self playMovie:video];
    }
}

- (IBAction)commentBtnPressed:(id)sender {
    [self hideSharePanel];
    UIViewController* vc =[[QSS04CommentListViewController alloc] initWithShow:self.showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)shareBtnPressed:(id)sender {
    [self showSharePanel];
}


#pragma mark - QSItemImageScrollViewDelegate
- (IBAction)likeBtnPressed:(id)sender {
    [self hideSharePanel];
    if ([QSShowUtil getIsLike:self.showDict]) {
        [SHARE_NW_ENGINE unlikeShow:self.showDict onSucceed:^{
            [self showSuccessHudWithText:@"取消喜欢成功"];
            [self bindWithDict:self.showDict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    } else {
        [SHARE_NW_ENGINE likeShow:self.showDict onSucceed:^{
            [self showSuccessHudWithText:@"喜欢成功"];
            [self bindWithDict:self.showDict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    }
}

- (IBAction)backBtnPressed:(id)sender {
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromLeft;
    tran.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseOut];
    [self.navigationController.view.layer addAnimation:tran forKey:@"transition_to_root"];
    [self.navigationController popViewControllerAnimated:NO];

}

- (IBAction)didTapModel:(id)sender {
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:self.showDict];
    QSP02ModelDetailViewController* vc = [[QSP02ModelDetailViewController alloc] initWithModel:peopleDict];
    [self.navigationController pushViewController:vc animated:YES];
    
}

- (IBAction)itemButtonPressed:(id)sender {
    QSS07ItemListViewController* vc = [[QSS07ItemListViewController alloc] initWithShow:self.showDict];
    vc.delegate = self;
    self.itemListVc = vc;
    vc.view.frame = self.view.bounds;
    [self.view addSubview:vc.view];
    [self setPlayModeBtnsHidden:YES];
    [self addChildViewController:vc];
    vc.view.alpha = 0.f;
    [UIView animateWithDuration:.3f animations:^{
        vc.view.alpha = 1.f;
    }];
    
}

- (void)didClickCloseBtn
{
    [UIView animateWithDuration:.3f animations:^{
        self.itemListVc.view.alpha = 0.f;
    } completion:^(BOOL finished) {
        [self setPlayModeBtnsHidden:NO];
        [self.itemListVc.view removeFromSuperview];
        [self.itemListVc removeFromParentViewController];
        self.itemListVc = nil;
    }];

}

- (IBAction)shareContainerPressed:(id)sender {
    [self hideSharePanel];
}


#pragma mark - Movie
#pragma mark Basic Control Method
- (void)startVideo
{
    self.movieController.view.hidden = NO;
    [self.movieController play];
    [self setPlayModeBtnsHidden:YES];
    self.videoContainerView.userInteractionEnabled = YES;
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
    [self.playBtn setImage:[UIImage imageNamed:@"s03_pause_btn"] forState:UIControlStateNormal];
}

- (void)stopMovie{
    if (self.movieController) {
        self.videoScreenShotImage = nil;
        self.movieController.view.hidden = YES;
        [self.movieController stop];
        [self setPlayModeBtnsHidden:NO];
        self.videoContainerView.userInteractionEnabled = NO;
        self.movieController.initialPlaybackTime = 0;
        [self updateShowImgScrollView];

    }
    [self.playBtn setImage:[UIImage imageNamed:@"s03_play_btn"] forState:UIControlStateNormal];
}

#pragma mark Init MovieController
-(void)playMovie:(NSString *)path{
    [self hideSharePanel];
    

    if (!self.movieController) {
        NSURL *url = [NSURL URLWithString:path];
        self.movieController = [[MPMoviePlayerController alloc] initWithContentURL:url];
        self.movieController.view.frame = self.videoContainerView.frame;
        self.movieController.scalingMode = MPMovieScalingModeAspectFill;
        self.movieController.controlStyle = MPMovieControlStyleNone;
        [self.videoContainerView addSubview:self.movieController.view];
        self.movieController.view.userInteractionEnabled = NO;

        //Gesture
        //Tap
        UITapGestureRecognizer* tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapVideo)];
        [self.videoContainerView addGestureRecognizer:tap];

        //Notification
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(myMovieFinishedCallback:)
                                                     name:MPMoviePlayerPlaybackDidFinishNotification
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleFirstShowHideVideo)
                                                     name:MPMoviePlayerPlaybackStateDidChangeNotification
                                                   object:nil];
        [self.movieController play];
    } else {
        [self startVideo];
    }
}

#pragma mark Gesture
- (void)didTapVideo
{
    [self hideSharePanel];
    if (self.movieController.playbackState == MPMoviePlaybackStatePaused || self.movieController.playbackState == MPMoviePlaybackStateStopped) {
    } else {
        [self pauseVideo];
    }
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

#pragma mark Helper
- (void)setPlayModeBtnsHidden:(BOOL)hidden
{
    self.backBtn.hidden = hidden;
    [self setBtnsHiddenExceptBack:hidden];

}
- (void)setBtnsHiddenExceptBack:(BOOL)hidden
{
    self.buttnPanel.hidden = hidden;
    self.modelContainer.hidden = hidden;
    self.playBtn.hidden = hidden;
}


- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (self.movieController) {
        CGRect rect = self.movieController.view.frame;
        rect.origin.y = self.videoContainerView.frame.origin.y - scrollView.contentOffset.y;
        self.movieController.view.frame = rect;
    }
    self.commentBtn.frame = CGRectMake(self.commentBtnRect.origin.x, self.commentBtnRect.origin.y - scrollView.contentOffset.y, self.commentBtnRect.size.width, self.commentBtnRect.size.height);
    self.playBtn.frame = CGRectMake(self.playBtnRect.origin.x, self.playBtnRect.origin.y - scrollView.contentOffset.y, self.playBtnRect.size.width, self.playBtnRect.size.height);
    self.shareBtn.frame = CGRectMake(self.shareBtnRect.origin.x, self.shareBtnRect.origin.y - scrollView.contentOffset.y, self.shareBtnRect.size.width, self.shareBtnRect.size.height);
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
    if (self.videoScreenShotImage) {
        [array addObject:self.videoScreenShotImage];
    }
    NSArray* previewArray = [QSShowUtil getShowVideoPreviewUrlArray:self.showDict];
    [array addObjectsFromArray:previewArray];
    self.showImageScrollView.imageUrlArray = array;
    [self.showImageScrollView scrollToPage:0];
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

#pragma mark - Share
- (void)showSharePanel
{
    [self.shareVc showSharePanel];
}
- (void)hideSharePanel
{
    [self.shareVc hideSharePanel];
}
- (void)didShareWeiboSuccess
{
    [self showSuccessHudWithText:@"分享成功"];
}
@end
