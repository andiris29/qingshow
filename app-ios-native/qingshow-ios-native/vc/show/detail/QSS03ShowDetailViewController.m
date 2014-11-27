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
    self.containerScrollView.contentSize = self.contentView.bounds.size;
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
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
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
    NSDictionary* itemDict = [QSShowUtil getItemFromShow:self.showDict AtIndex:index];
    UIViewController* vc = [[QSS03ItemDetailViewController alloc] initWithItemDict:itemDict];
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
    }
    self.movieController.view.hidden = NO;
    
    self.movieController.view.frame = self.videoContainerView.frame;
    self.movieController.controlStyle = MPMovieControlStyleEmbedded;
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(myMovieFinishedCallback:)
                                                 name:MPMoviePlayerPlaybackDidFinishNotification
                                               object:nil];
    [self.movieController play];

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



@end
