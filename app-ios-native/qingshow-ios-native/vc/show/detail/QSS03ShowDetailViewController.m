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

@interface QSS03ShowDetailViewController ()

@property (strong, nonatomic) QSSingleImageScrollView* showImageScrollView;
@property (strong, nonatomic) QSItemImageScrollView* itemImageScrollView;

@property (strong, nonatomic) NSDictionary* showDict;
@property (strong, nonatomic) MPMoviePlayerController* movieController;
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
    
    self.showImageScrollView = [[QSSingleImageScrollView alloc] initWithFrame:CGRectMake(0, 0, 300, 400)];
    [self.showContainer addSubview:self.showImageScrollView];
    

    
    self.itemImageScrollView = [[QSItemImageScrollView alloc] initWithFrame:CGRectMake(0, 0, 300, 120)];
    [self.itemContainer addSubview:self.itemImageScrollView];
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
#warning 未完成
    NSLog(@"share btn pressed");
}


#pragma mark - QSItemImageScrollViewDelegate
- (void)didTapItemAtIndex:(int)index
{
    NSArray* items = [QSShowUtil getItems:self.showDict];
    UIViewController* vc = [[QSS03ItemDetailViewController alloc] initWithItems:items];
    [self presentViewController:vc animated:YES completion:nil];
}


- (IBAction)favorBtnPressed:(id)sender {
    
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
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hidecontrol)
                                                 name:MPMoviePlayerLoadStateDidChangeNotification
                                               object:nil];
}
- (void) hidecontrol {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerNowPlayingMovieDidChangeNotification object:nil];
    [self.movieController setControlStyle:MPMovieControlStyleEmbedded];
    
}

- (void) didEnterFullScreen
{
    self.movieController.scalingMode = MPMovieScalingModeAspectFill;
}

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
}

- (void)didTapVideo
{
    if (self.movieController.playbackState == MPMoviePlaybackStatePaused) {
        [self.movieController play];
    } else {
        [self.movieController pause];
    }
}
- (void)didPinch:(UIPinchGestureRecognizer*)g
{
    if (!self.movieController.isFullscreen && g.scale >= 1.5) {
//        self.movieController.fullscreen = YES;
        [self.movieController setFullscreen:YES animated:YES];
        self.movieController.scalingMode = MPMovieScalingModeAspectFill;
    }
}
@end
