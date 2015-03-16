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
#import "QSS04CommentListViewController.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"
#import "QSCommonUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSNetworkKit.h"
#import "QSItemUtil.h"
#import "QSImageNameUtil.h"

#import "UIViewController+ShowHud.h"
#import <QuartzCore/QuartzCore.h>
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"
#import "UIView+ScreenShot.h"

#define PAGE_ID @"S03 - 秀"

@interface QSS03ShowDetailViewController ()

@property (strong, nonatomic) NSDictionary* showDict;

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
    
    self.headIconImageView.layer.cornerRadius = self.headIconImageView.frame.size.width / 2;
    self.headIconImageView.layer.masksToBounds = YES;
    self.headIconImageView.layer.borderWidth = 1;
    self.headIconImageView.layer.borderColor = [UIColor whiteColor].CGColor;
    
    self.shareVc = [[QSShareViewController alloc] init];
    self.shareVc.delegate = self;
    [self.view addSubview:self.shareVc.view];
    self.shareVc.view.frame = self.view.bounds;
    
    [self bindWithDict:self.showDict];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self bindExceptImageWithDict:self.showDict];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    if (!self.itemListVc) {
        __weak QSS03ShowDetailViewController* weakSelf = self;
        if (self.showDict) {
            [weakSelf bindExceptImageWithDict:self.showDict];
        }
        [SHARE_NW_ENGINE queryShowDetail:self.showDict onSucceed:^(NSDictionary * dict) {
            weakSelf.showDict = dict;
            [weakSelf bindExceptImageWithDict:dict];
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

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)dict
{
    [self bindExceptImageWithDict:dict];
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
- (IBAction)playOrPauseBtnPressed:(id)sender {
    [self hideSharePanel];
    [super playOrPauseBtnPressed:sender];
}

- (IBAction)commentBtnPressed:(id)sender {
    [self hideSharePanel];
    UIViewController* vc =[[QSS04CommentListViewController alloc] initWithShow:self.showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)shareBtnPressed:(id)sender {
    [self showSharePanel];
}


- (IBAction)likeBtnPressed:(id)sender {
    [self hideSharePanel];
    if ([QSShowUtil getIsLike:self.showDict]) {
        [SHARE_NW_ENGINE unlikeShow:self.showDict onSucceed:^{
            [self showSuccessHudWithText:@"取消喜欢成功"];
            [self bindExceptImageWithDict:self.showDict];
        } onError:^(NSError *error) {
            [self bindExceptImageWithDict:self.showDict];
            [self handleError:error];
        }];
    } else {
        [SHARE_NW_ENGINE likeShow:self.showDict onSucceed:^{
            [self showSuccessHudWithText:@"喜欢成功"];
            [self bindExceptImageWithDict:self.showDict];
        } onError:^(NSError *error) {
            [self bindExceptImageWithDict:self.showDict];
            [self handleError:error];
        }];
    }
}


#pragma mark - 
- (IBAction)didTapModel:(id)sender {
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:self.showDict];
    QSP02ModelDetailViewController* vc = [[QSP02ModelDetailViewController alloc] initWithModel:peopleDict];
    [self.navigationController pushViewController:vc animated:YES];
    
}

#pragma mark -
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

- (void)didClickItemListCloseBtn
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

-(void)playMovie:(NSString *)path{
    [self hideSharePanel];
    [super playMovie:path];
}

#pragma mark - Share
- (void)showSharePanel
{
    NSString* showId = [QSCommonUtil getIdOrEmptyStr:self.showDict];
    NSString* urlStr = [NSString stringWithFormat:@"http://chingshow.com/app-web?action=shareShow&_id=%@", showId];
    [self.shareVc showSharePanelWithUrl:urlStr];
}
- (void)hideSharePanel
{
    [self.shareVc hideSharePanel];
}
- (void)didShareWeiboSuccess
{
    [self showSuccessHudWithText:@"分享成功"];
}

#pragma mark - Override
#pragma mark Data
- (NSArray*)generateImagesData
{
    return [QSImageNameUtil generate2xImageNameUrlArray:[QSShowUtil getShowVideoPreviewUrlArray:self.showDict]];;
}
- (NSString*)generateVideoPath
{
    return self.showDict[@"video"];
}

#pragma mark Btn
- (void)setPlayModeBtnsHidden:(BOOL)hidden
{
    self.backBtn.hidden = hidden;
    [self setBtnsHiddenExceptBackAndPlay:hidden];
    
}
- (void)setBtnsHiddenExceptBack:(BOOL)hidden
{
    [self setBtnsHiddenExceptBackAndPlay:hidden];
    self.playBtn.hidden = hidden;
//    self.pauseBtn.hidden = hidden;
}
- (void)setBtnsHiddenExceptBackAndPlay:(BOOL)hidden
{
    self.buttnPanel.hidden = hidden;
    self.modelContainer.hidden = hidden;
}

#pragma mark Mob
- (void)logMobPlayVideo:(NSTimeInterval)playbackTime
{
    [MobClick event:@"playVideo" attributes:@{@"showId" : self.showDict[@"_id"], @"length": @(playbackTime).stringValue} durations:(int)(playbackTime * 1000)];
}


@end
