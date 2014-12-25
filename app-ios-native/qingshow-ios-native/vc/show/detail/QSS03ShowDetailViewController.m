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
#import "QSCommentListViewController.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import <MediaPlayer/MediaPlayer.h>
#import "QSNetworkKit.h"
#import "QSItemUtil.h"
#import <Social/Social.h>
#import "UIViewController+ShowHud.h"
#import <QuartzCore/QuartzCore.h>
#import "QSUserManager.h"
#import "WeiboSDK.h"
#import "WXApi.h"
#import "QSSharePlatformConst.h"
#import "QSS03ItemListViewController.h"
#import "UIViewController+Network.h"

@interface QSS03ShowDetailViewController ()

@property (strong, nonatomic) QSSingleImageScrollView* showImageScrollView;
@property (strong, nonatomic) QSItemImageScrollView* itemImageScrollView;

@property (strong, nonatomic) NSDictionary* showDict;
@property (strong, nonatomic) MPMoviePlayerController* movieController;


@property (assign, nonatomic) CGRect commentBtnRect;
@property (assign, nonatomic) CGRect shareBtnRect;
@property (assign, nonatomic) CGRect playBtnRect;

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
    self.showImageScrollView.pageControl.hidden = YES;
    [self.showContainer addSubview:self.showImageScrollView];
    
//    self.itemImageScrollView = [[QSItemImageScrollView alloc] initWithFrame:CGRectMake(0, 0, 300, 120)];
//    [self.itemContainer addSubview:self.itemImageScrollView];
//    self.itemContainer.layer.cornerRadius = 4;
//    self.itemContainer.layer.masksToBounds = YES;
    self.itemImageScrollView.delegate = self;

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
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(weiboAuthorizeNotiHander:) name:kWeiboAuthorizeResultNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(weiboSendMessageNotiHandler:) name:kWeiboSendMessageResultNotification object:nil];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.navigationController.navigationBarHidden = YES;
    
    __weak QSS03ShowDetailViewController* weakSelf = self;
    [SHARE_NW_ENGINE queryShowDetail:self.showDict onSucceed:^(NSDictionary * dict) {
        weakSelf.showDict = dict;
        [weakSelf bindWithDict:dict];
    } onError:^(NSError *error) {

    }];

}
- (void)viewWillDisappear:(BOOL)animated
{
    [self hideSharePanel];
    [super viewWillDisappear:animated];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self stopMovie];
    
}

- (void)bindWithDict:(NSDictionary*)dict
{
    self.favorBtn.highlighted = [QSShowUtil getIsLike:dict];
    //Model
    
    NSDictionary* peopleInfo = [QSShowUtil getPeopleFromShow:dict];
    
    NSURL* iconUrl = [QSPeopleUtil getHeadIconUrl:peopleInfo];
    [self.headIconImageView setImageFromURL:iconUrl];
    
    self.nameLabel.text = [QSPeopleUtil getName:peopleInfo];
    self.detailLabel.text = [QSPeopleUtil getDetailDesc:peopleInfo];
    self.contentLabel.text = [QSPeopleUtil getStatus:peopleInfo];
    
    //Image
    NSArray* previewArray = [QSShowUtil getShowVideoPreviewUrlArray:dict];
    self.showImageScrollView.imageUrlArray = previewArray;
    NSArray* itemUrlArray = [QSShowUtil getItemsImageUrlArrayFromShow:dict];
    self.itemImageScrollView.imageUrlArray = itemUrlArray;
    [self.commentBtn setTitle:[QSShowUtil getNumberCommentsDescription:dict] forState:UIControlStateNormal];
    [self.favorBtn setTitle:[QSShowUtil getNumberLikeDescription:dict] forState:UIControlStateNormal];
    [self.itemBtn setTitle:[QSShowUtil getNumberItemDescription:self.showDict] forState:UIControlStateNormal];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
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
    UIViewController* vc =[[QSCommentListViewController alloc] initWithShow:self.showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)shareBtnPressed:(id)sender {
    [self showSharePanel];
}


#pragma mark - QSItemImageScrollViewDelegate
- (void)didTapItemAtIndex:(int)index
{
    NSArray* items = [QSShowUtil getItems:self.showDict];
    UIViewController* vc = [[QSS03ItemDetailViewController alloc] initWithItems:items];
    [self presentViewController:vc animated:YES completion:nil];
}


- (IBAction)likeBtnPressed:(id)sender {
    [self hideSharePanel];
    if ([QSShowUtil getIsLike:self.showDict]) {
        [SHARE_NW_ENGINE unlikeShow:self.showDict onSucceed:^{
            [self showSuccessHudWithText:@"unlike succeed"];
            [self bindWithDict:self.showDict];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    } else {
        [SHARE_NW_ENGINE likeShow:self.showDict onSucceed:^{
            [self showSuccessHudWithText:@"like succeed"];
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
    UIViewController* vc = [[QSS03ItemListViewController alloc] initWithShow:self.showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)shareContainerPressed:(id)sender {
    [self hideSharePanel];
}


#pragma mark - Movie
-(void)playMovie:(NSString *)path{
    NSURL *url = [NSURL URLWithString:path];
    if (!self.movieController) {
        self.movieController = [[MPMoviePlayerController alloc] initWithContentURL:url];
    }
    if (self.movieController.view.superclass) {

        [self.videoContainerView addSubview:self.movieController.view];

        self.movieController.view.userInteractionEnabled = NO;
        
        UITapGestureRecognizer* tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapVideo)];
        [self.videoContainerView addGestureRecognizer:tap];
//        UIPinchGestureRecognizer* pinch = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(didPinch:)];
//        [self.videoContainerView addGestureRecognizer:pinch];
        
//        UIPinchGestureRecognizer*  ges = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(didPinch:)];
//        [self.movieController.view addGestureRecognizer:ges];
        self.movieController.view.frame = self.videoContainerView.frame;
        self.movieController.scalingMode = MPMovieScalingModeAspectFill;
        
        self.movieController.controlStyle = MPMovieControlStyleNone;
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(myMovieFinishedCallback:)
                                                     name:MPMoviePlayerPlaybackDidFinishNotification
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleShowHideVideo)
                                                     name:MPMoviePlayerPlaybackStateDidChangeNotification
                                                   object:nil];
        self.videoContainerView.userInteractionEnabled = NO;
        [self.movieController play];
    } else {
        [self didTapVideo];
    }

    
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(didEnterFullScreen)
//                                                 name:MPMoviePlayerDidEnterFullscreenNotification
//                                               object:nil];


    
//    [self setCommentSharePlayButtonHidden:YES];

}
- (void)didEnd
{
    [self.movieController setFullscreen:NO animated:YES];
    self.movieController.initialPlaybackTime = 0;
}
- (void)didEnterFullScreen
{
    self.movieController.view.userInteractionEnabled = YES;
    self.movieController.scalingMode = MPMovieScalingModeAspectFill;
    [self.movieController setControlStyle:MPMovieControlStyleFullscreen];
}
- (void)setCommentSharePlayButtonHidden:(BOOL)hidden
{
    self.buttnPanel.hidden = hidden;
    self.backBtn.hidden = hidden;
    self.modelContainer.hidden = hidden;
    self.playBtn.hidden = hidden;

}
- (void)didExitFullScreen
{
    self.movieController.view.userInteractionEnabled = NO;
    [self.movieController setControlStyle:MPMovieControlStyleNone];
}
//- (void) hidecontrol {
//    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerNowPlayingMovieDidChangeNotification object:nil];
//    [self.movieController setControlStyle:MPMovieControlStyleEmbedded];
    
//}

- (void)stopMovie{
    if (self.movieController) {
        [self.movieController stop];
        [self.movieController.view removeFromSuperview];
        self.videoContainerView.userInteractionEnabled = NO;
    }
}

-(void)myMovieFinishedCallback:(NSNotification*)notify
{
    [self handleShowHideVideo];
    [self stopMovie];

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

- (void)handleShowHideVideo
{
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerPlaybackStateDidChangeNotification object:nil];
    
    self.videoContainerView.userInteractionEnabled = YES;
    NSLog(@"%ld",self.movieController.playbackState);
    if (self.movieController.playbackState == MPMoviePlaybackStatePaused || self.movieController.playbackState == MPMoviePlaybackStateStopped) {
        [self setCommentSharePlayButtonHidden:NO];
    } else {
        [self setCommentSharePlayButtonHidden:YES];
    }

}
- (void)didTapVideo
{
    [self hideSharePanel];
    if (self.movieController.playbackState == MPMoviePlaybackStatePaused || self.movieController.playbackState == MPMoviePlaybackStateStopped) {
        [self.movieController play];
        [self setCommentSharePlayButtonHidden:YES];
    } else {
        [self.movieController pause];
        [self setCommentSharePlayButtonHidden:NO];
    }
}
- (void)didPinch:(UIPinchGestureRecognizer*)g
{
    if (!self.movieController.isFullscreen && g.scale >= 1.5) {
        [self.movieController setFullscreen:YES animated:YES];
        self.movieController.scalingMode = MPMovieScalingModeAspectFill;
    } else if (self.movieController.isFullscreen && g.scale <= .5)
    {
        [self.movieController setFullscreen:NO animated:YES];
    }
}


#pragma mark - Share
- (void)showSharePanel
{
    if (self.shareContainer.hidden == NO && self.sharePanel.hidden == NO){
        return;
    }
    [self.view bringSubviewToFront:self.sharePanel];
    self.shareContainer.hidden = NO;
    self.sharePanel.hidden = NO;
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromTop;
    tran.duration = 0.2f;
    [self.sharePanel.layer addAnimation:tran forKey:@"ShowAnimation"];

}
- (void)hideSharePanel
{
    if (self.shareContainer.hidden == YES && self.sharePanel.hidden == YES) {
        return;
    }
    self.shareContainer.hidden = YES;
    self.sharePanel.hidden = YES;
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromBottom;
    tran.duration = 0.2f;
    [self.sharePanel.layer addAnimation:tran forKey:@"ShowAnimation"];
}

- (IBAction)shareWeiboPressed:(id)sender {
    [self hideSharePanel];
    
    NSString* weiboAccessToken = [QSUserManager shareUserManager].weiboAccessToken;
    WBAuthorizeRequest *request = [WBAuthorizeRequest request];
    request.redirectURI = kWeiboRedirectURI;
    request.scope = @"all";
    request.userInfo = nil;
    
    WBMessageObject *message = [WBMessageObject message];
    WBWebpageObject* webPage = [WBWebpageObject object];
    webPage.objectID = @"qingshow_webpage_id";
    webPage.title = @"倾秀";
    webPage.description = @"qingshow desc";
    webPage.webpageUrl = @"http://chingshow.com/web-mobile/src/index.html#?entry=S03&_id=";
    webPage.thumbnailData = UIImagePNGRepresentation([UIImage imageNamed:@"share_icon"]);
    
//    NSData *imageData = UIImagePNGRepresentation([UIImage imageNamed:@"gray_clock"], 0.5);

    message.mediaObject = webPage;
    WBSendMessageToWeiboRequest *msgRequest = [WBSendMessageToWeiboRequest requestWithMessage:message authInfo:request access_token:weiboAccessToken];
    message.text = @"倾秀";
    [WeiboSDK sendRequest:msgRequest];
}


- (void)weiboSendMessageNotiHandler:(NSNotification*)notification
{
    if (WeiboSDKResponseStatusCodeSuccess == ((NSNumber*)notification.userInfo[@"statusCode"]).integerValue) {
        [self showSuccessHudWithText:@"分享成功"];
    }
}
- (void)weiboAuthorizeNotiHander:(NSNotification*)notification
{
    if (WeiboSDKResponseStatusCodeSuccess == ((NSNumber*)notification.userInfo[@"statusCode"]).integerValue) {
        [self shareWeiboPressed:nil];
    }
}

- (IBAction)shareWechatPressed:(id)sender {
    WXMediaMessage *message = [WXMediaMessage message];
    message.title = @"qingshow";
    message.description = @"qingshow";
    
    WXWebpageObject *ext = [WXWebpageObject object];

    ext.webpageUrl = @"http://chingshow.com/web-mobile/src/index.html#?entry=S03&_id=";
    
    message.mediaObject = ext;
    
    SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
    req.bText = NO;
    req.message = message;
    req.scene = WXSceneTimeline;
    
    [WXApi sendReq:req];
    [self hideSharePanel];
}
- (IBAction)shareWechatFriendPressed:(id)sender {
    WXMediaMessage *message = [WXMediaMessage message];
    message.title = @"qingshow";
    message.description = @"qingshow";
    
    WXWebpageObject *ext = [WXWebpageObject object];
    
    ext.webpageUrl = @"http://chingshow.com/web-mobile/src/index.html#?entry=S03&_id=";
    
    message.mediaObject = ext;
    
    SendMessageToWXReq* req = [[SendMessageToWXReq alloc] init];
    req.bText = NO;
    req.message = message;
    req.scene = WXSceneSession;
    
    [WXApi sendReq:req];
    [self hideSharePanel];
}
- (IBAction)shareCancelPressed:(id)sender {
    [self hideSharePanel];
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self hideSharePanel];
}
@end
