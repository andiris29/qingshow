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
#import "QSNetworkEngine.h"
#import "QSItemUtil.h"
#import <Social/Social.h>
#import "UIViewController+ShowHud.h"

#import "QSUserManager.h"
#import "WeiboSDK.h"
#import "QSSharePlatformConst.h"

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
    self.containerScrollView.translatesAutoresizingMaskIntoConstraints = NO;
    self.contentView.translatesAutoresizingMaskIntoConstraints = NO;
    self.containerScrollView.contentSize = self.contentView.frame.size;
    [self.containerScrollView addSubview:self.contentView];
    self.contentView.layer.cornerRadius = 4;
    self.contentView.layer.masksToBounds = YES;
    self.modelContainer.layer.cornerRadius = 4;
    self.modelContainer.layer.masksToBounds = YES;
    self.itemContainer.layer.cornerRadius = 4;
    self.itemContainer.layer.masksToBounds = YES;
    self.showContainer.layer.cornerRadius = 4;
    self.showContainer.layer.masksToBounds = YES;
    self.itemDesContainer.layer.cornerRadius = 4;
    self.itemDesContainer.layer.masksToBounds = YES;
    
    self.showImageScrollView = [[QSSingleImageScrollView alloc] initWithFrame:CGRectMake(0, 0, 300, 400)];
    [self.showContainer addSubview:self.showImageScrollView];
    
    self.itemImageScrollView = [[QSItemImageScrollView alloc] initWithFrame:CGRectMake(0, 0, 300, 120)];
    [self.itemContainer addSubview:self.itemImageScrollView];
    self.itemContainer.layer.cornerRadius = 4;
    self.itemContainer.layer.masksToBounds = YES;
    self.itemImageScrollView.delegate = self;

    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
    
    
    self.headIconImageView.layer.cornerRadius = self.headIconImageView.frame.size.width / 2;
    self.headIconImageView.layer.masksToBounds = YES;
    
    [self bindWithDict:self.showDict];
    
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    
    __weak QSS03ShowDetailViewController* weakSelf = self;
    [SHARE_NW_ENGINE queryShowDetail:self.showDict onSucceed:^(NSDictionary * dict) {
        weakSelf.showDict = dict;
        [weakSelf bindWithDict:dict];
    } onError:^(NSError *error) {
        
    }];
    
    self.commentBtnRect = self.commentBtn.frame;
    self.shareBtnRect = self.shareBtn.frame;
    self.playBtnRect = self.playBtn.frame;
    
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(weiboAuthorizeNotiHander:) name:kWeiboAuthorizeResultNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(weiboSendMessageNotiHandler:) name:kWeiboSendMessageResultNotification object:nil];
    
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}
- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [self stopMovie];
    
}

- (void)bindWithDict:(NSDictionary*)dict
{
    //Model
    NSDictionary* peopleInfo = dict[@"modelRef"];
    
    NSString* iconPath = peopleInfo[@"portrait"];
    NSURL* iconUrl = [NSURL URLWithString:iconPath];
    [self.headIconImageView setImageFromURL:iconUrl];
    
    self.nameLabel.text = peopleInfo[@"name"];
    self.detailLabel.text = [QSPeopleUtil buildModelStatusString:peopleInfo];
    NSDictionary* modelInfo = peopleInfo[@"modelInfo"];
    NSString* status = nil;
    if (modelInfo) {
        status = modelInfo[@"status"];
        status = status ? status : @"";
    }
    self.contentLabel.text = status;
    self.favorNumberLabel.text = [QSPeopleUtil buildNumLikeString:peopleInfo];
    
    //Image
    NSArray* previewArray = [QSShowUtil getShowVideoPreviewUrlArray:dict];
    self.showImageScrollView.imageUrlArray = previewArray;
    NSArray* itemUrlArray = [QSShowUtil getItemsImageUrlArrayFromShow:dict];
    self.itemImageScrollView.imageUrlArray = itemUrlArray;
    [self.commentBtn setTitle:[QSShowUtil getNumberCommentsDescription:dict] forState:UIControlStateNormal];
    self.favorNumberLabel.text = [QSShowUtil getNumberFavorDescription:dict];
    
    //ItemDes
    NSArray* items = [QSShowUtil getItems:dict];
    NSAttributedString* itemDes = [QSItemUtil getItemsAttributedDescription:items];
    self.itemDesLabel.attributedText = itemDes;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)playBtnPressed:(id)sender {
    NSString* video = self.showDict[@"video"];
    if (video) {
        [self playMovie:video];
    }
}

- (IBAction)commentBtnPressed:(id)sender {
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


- (IBAction)favorBtnPressed:(id)sender {
    if ([QSShowUtil getIsLike:self.showDict]) {
        [SHARE_NW_ENGINE unlikeShow:self.showDict onSucceed:^{
            [self showSuccessHudWithText:@"unlike succeed"];
            //            [self.delegateObj reloadData];
        } onError:^(NSError *error) {
            [self showErrorHudWithError:error];
        }];
    } else {
        [SHARE_NW_ENGINE likeShow:self.showDict onSucceed:^{
            [self showSuccessHudWithText:@"like succeed"];
            //            [self.delegateObj reloadData];
        } onError:^(NSError *error) {
            [self showErrorHudWithError:error];
        }];
    }
}

- (IBAction)didTapModel:(id)sender {
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:self.showDict];
    QSP02ModelDetailViewController* vc = [[QSP02ModelDetailViewController alloc] initWithModel:peopleDict];
    [self.navigationController pushViewController:vc animated:YES];
    
}


#pragma mark - Movie
-(void)playMovie:(NSString *)path{
    NSURL *url = [NSURL URLWithString:path];
    if (!self.movieController) {
        self.movieController = [[MPMoviePlayerController alloc] initWithContentURL:url];
        [self.view addSubview:self.movieController.view];
        self.movieController.view.userInteractionEnabled = NO;
        
        UITapGestureRecognizer* tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapVideo)];
        [self.videoContainerView addGestureRecognizer:tap];
        UIPinchGestureRecognizer* pinch = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(didPinch:)];
        [self.videoContainerView addGestureRecognizer:pinch];
        [self.view bringSubviewToFront:self.commentBtn];
        [self.view bringSubviewToFront:self.shareBtn];
        [self.view bringSubviewToFront:self.playBtn];
        
        UIPinchGestureRecognizer*  ges = [[UIPinchGestureRecognizer alloc] initWithTarget:self action:@selector(didPinch:)];
        [self.movieController.view addGestureRecognizer:ges];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didExitFullScreen)
                                                     name:MPMoviePlayerDidExitFullscreenNotification
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didEnd)
                                                     name:MPMoviePlayerPlaybackDidFinishNotification
                                                   object:nil];
    }
    self.movieController.view.frame = self.videoContainerView.frame;
//    self.movieController.view.userInteractionEnabled = NO;
    self.movieController.view.hidden = NO;
    self.movieController.scalingMode = MPMovieScalingModeAspectFill;

    self.movieController.controlStyle = MPMovieControlStyleNone;
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(myMovieFinishedCallback:)
                                                 name:MPMoviePlayerPlaybackDidFinishNotification
                                               object:nil];
//    [[NSNotificationCenter defaultCenter] addObserver:self
//                                             selector:@selector(didEnterFullScreen)
//                                                 name:MPMoviePlayerDidEnterFullscreenNotification
//                                               object:nil];
    [self.movieController play];

    [self scrollViewDidScroll:self.containerScrollView];


    
    [self setCommentSharePlayButtonHidden:YES];
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
    self.commentBtn.hidden = hidden;
    self.playBtn.hidden = hidden;
    self.shareBtn.hidden = hidden;
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
        self.movieController.view.hidden = YES;
    }
}

-(void)myMovieFinishedCallback:(NSNotification*)notify
{
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

- (void)didTapVideo
{
    if (self.movieController.playbackState == MPMoviePlaybackStatePaused) {
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
    if (self.sharePanel.hidden == NO){
        return;
    }
    self.sharePanel.hidden = NO;
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromTop;
    [self.sharePanel.layer addAnimation:tran forKey:@"ShowAnimation"];
}
- (void)hideSharePanel
{
    if (self.sharePanel.hidden == YES) {
        return;
    }
    self.sharePanel.hidden = YES;
    CATransition* tran = [[CATransition alloc] init];
    tran.type = kCATransitionPush;
    tran.subtype = kCATransitionFromBottom;
    [self.sharePanel.layer addAnimation:tran forKey:@"ShowAnimation"];
}

- (IBAction)shareWeiboPressed:(id)sender {
    NSString* weiboAccessToken = [QSUserManager shareUserManager].weiboAccessToken;
    WBAuthorizeRequest *request = [WBAuthorizeRequest request];
    request.redirectURI = kWeiboRedirectURI;
    request.scope = @"all";
    request.userInfo = nil;
    if (!weiboAccessToken || !weiboAccessToken.length) {

        [WeiboSDK sendRequest:request];
    } else {
        WBMessageObject *message = [WBMessageObject message];
        WBWebpageObject* webPage = [WBWebpageObject object];
        webPage.objectID = @"qingshow_webpage_id";
        webPage.title = @"倾秀";
        webPage.description = @"qingshow desc";
        webPage.webpageUrl = @"http://chingshow.com/web-mobile/src/index.html#?entry=S03&_id=";
        message.mediaObject = webPage;
        WBSendMessageToWeiboRequest *msgRequest = [WBSendMessageToWeiboRequest requestWithMessage:message authInfo:request access_token:weiboAccessToken];
        [WeiboSDK sendRequest:msgRequest];
    }
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
}
- (IBAction)shareCancelPressed:(id)sender {
    [self hideSharePanel];
}

@end
