//
//  QSS03ShowDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ShowDetailViewController.h"
#import "QSSingleImageScrollView.h"
#import "QSS04CommentListViewController.h"
#import "QSShowUtil.h"
#import "QSPeopleUtil.h"
#import "QSEntityUtil.h"
#import "QSDateUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSNetworkKit.h"
#import "QSItemUtil.h"
#import "QSImageNameUtil.h"
#import "QSPromotionUtil.h"
#import "QSShareService.h"
#import "QSNetworkEngine+ShareService.h"
#import "QSShareUtil.h"
#import "QSS11ItemBuyViewController.h"
#import "QSItemTagView.h"
#import "QSLayoutUtil.h"

#import "UIViewController+ShowHud.h"
#import <QuartzCore/QuartzCore.h>
#import "QSUserManager.h"
#import "UIViewController+QSExtension.h"

#import "QSUserManager.h"
#import "QSU01UserDetailViewController.h"

#define PAGE_ID @"S03 - 秀"
#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)
@interface QSS03ShowDetailViewController ()

@property (strong, nonatomic) NSDictionary* showDict;
@property (strong, nonatomic) NSString* showId;
@property (strong, nonatomic) NSDictionary *oldShowDict;
@property (strong, nonatomic) QSShareViewController* shareVc;
@property (strong, nonatomic) NSMutableArray* itemLabelArray;
@property (weak, nonatomic) IBOutlet UIImageView *rankImgView;
@property (assign, nonatomic) BOOL showShouldLabel;

@end

@implementation QSS03ShowDetailViewController
#pragma mark - Init Method
- (instancetype)initWithShowId:(NSString*)showId {
    self = [self initWithNibName:@"QSS03ShowDetailViewController" bundle:nil];
    if (self) {
        self.showId = showId;
        self.showDeletedBtn = NO;
        self.showShouldLabel = YES;
    }
    return self;
}
- (instancetype)initWithShow:(NSDictionary*)showDict
{
    self = [self initWithShowId:[QSEntityUtil getIdOrEmptyStr:showDict]];
    if (self) {
        self.showDict = [showDict mutableCopy];
        self.oldShowDict = showDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {

    [super viewDidLoad];
    self.itemLabelArray = [@[] mutableCopy];
    self.shareVc = [[QSShareViewController alloc] init];
    self.shareVc.delegate = self;
    [self.view addSubview:self.shareVc.view];
    self.shareVc.view.frame = self.view.bounds;
    if (self.showDict) {
        [self bindWithDict:self.showDict];
    }

    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    
    self.headIconImageView.layer.cornerRadius = self.headIconImageView.bounds.size.height / 2;
    self.headIconImageView.layer.masksToBounds = YES;
    self.headIconImageView.userInteractionEnabled = YES;
    self.bonusLabel.layer.cornerRadius = self.bonusLabel.bounds.size.height / 2;
    self.bonusLabel.layer.borderWidth = 0.1f;
    self.bonusLabel.layer.borderColor = [UIColor whiteColor].CGColor;
    [self.headIconImageView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapHeadIcon:)]];

    [SHARE_NW_ENGINE viewShow:self.showDict onSucceed:^{
        [QSShowUtil addNumberView:1l forShow:self.oldShowDict];
    } onError:nil];
    
    UITapGestureRecognizer* tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapLabelContainer:)];
    [self.coverLabelContainerView addGestureRecognizer:tapGes];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    __weak QSS03ShowDetailViewController* weakSelf = self;
    if (self.showDict) {
        [weakSelf bindExceptImageWithDict:self.showDict];
    }
    
    if (!self.showDict) {
        [SHARE_NW_ENGINE queryShowIdDetail:self.showId onSucceed:^(NSDictionary * dict) {
            weakSelf.showDict = dict;
            [weakSelf bindWithDict:dict];
            self.discountContainer.hidden = YES;
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    } else {
        [SHARE_NW_ENGINE queryShowDetail:self.showDict onSucceed:^(NSDictionary * dict) {
            weakSelf.showDict = dict;
            [weakSelf bindExceptImageWithDict:dict];
            NSDictionary* promotionDict = [QSShowUtil getPromotionRef:dict];
            if (!promotionDict) {
                self.discountContainer.hidden = YES;
            } else {
                if (![QSPromotionUtil getIsEnabled:promotionDict]) {
                    [self showDiscountContainer];
                } else {
                    self.discountContainer.hidden = YES;
                }
            }
        } onError:^(NSError *error) {
            
        }];
    }
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    
}


- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self hideSharePanel];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    
    float ratioX = w/150;
    float ratioY = h/277;
    float leftMargin = 6*ratioX;
    float topMargin = 54*ratioY;
    float weight = 138*ratioX;
    float height = 191.8*ratioY;
    
    self.coverImageView.frame =
    CGRectMake(
                leftMargin,
               topMargin,
                weight,
              height);
    
}

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)dict
{
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:dict];
    NSString* peopleId = [QSEntityUtil getIdOrEmptyStr:peopleDict];
    [SHARE_NW_ENGINE queryPeopleDetail:peopleId onSucceed:^(NSDictionary *detailPeopleDict) {
        [self updatePeopleInfo:detailPeopleDict];
    } onError:nil];
    
    [self bindExceptImageWithDict:dict];
    
    NSArray* array = [self generateImagesData];
    if (array && array.count) {
        self.scrollViewContainer.hidden = NO;
        self.coverContainer.hidden = YES;
        [self updateShowImgScrollView];
    } else {
        self.scrollViewContainer.hidden = YES;
        self.coverContainer.hidden = NO;
        [self updateCover];
    }
}

- (void)updatePeopleInfo:(NSDictionary*)peopleDict {
    if (!peopleDict) {
        self.headIconImageView.hidden = NO;
        self.rankImgView.hidden = NO;
        self.modelNameLabel.hidden = NO;
    } else {
        if (self.showDeletedBtn) {
            //当前用户
            self.headIconImageView.hidden = YES;
            self.rankImgView.hidden = YES;
            self.modelNameLabel.hidden = YES;
            self.bonusLabel.hidden = YES;
            self.releaseDateLabel.hidden = NO;
            
        } else {
            self.headIconImageView.hidden = NO;
            [self.headIconImageView setImageFromURL:[QSPeopleUtil getHeadIconUrl:peopleDict type:QSImageNameType100] beforeCompleteBlock:nil animation:NO];
            self.rankImgView.hidden = NO;
            self.rankImgView.image = [QSPeopleUtil rankImgView:peopleDict];
            self.modelNameLabel.hidden = NO;
            self.bonusLabel.hidden = NO;
            NSNumber* bonusNumber = [QSPeopleUtil getTotalBonus:peopleDict];
            if (bonusNumber) {
                self.bonusLabel.text = [NSString stringWithFormat:@" 收益:￥%.2f",bonusNumber.floatValue];
            }
            CGSize size = [QSLayoutUtil sizeForString:self.bonusLabel.text withMaxWidth:INFINITY height:self.bonusLabel.bounds.size.height font:self.bonusLabel.font];
            CGRect rect = self.bonusLabel.frame;
            rect.size.width = size.width + 10.f;
            self.bonusLabel.frame = rect;
            self.modelNameLabel.text = [QSPeopleUtil getNickname:peopleDict];
        }
    }
}

- (void)updateCover {
    [self.coverImageView setImageFromURL:[QSShowUtil getCoverUrl:self.showDict] placeHolderImage:nil animation:NO complete:^{
        [self _updateLabel];
    }];
    [self.coverBackgroundImageView setImageFromURL:[QSShowUtil getCoverBackgroundUrl:self.showDict] beforeCompleteBlock:nil animation:NO];
    [self.coverForegroundImageView setImageFromURL:[QSShowUtil getCoverForegroundUrl:self.showDict]];
}

- (void)_updateLabel {
    self.showShouldLabel = YES;
    BOOL fShowLabel = self.showShouldLabel && self.coverImageView.image;
    CGPoint origin = self.coverImageView.frame.origin;
    CGSize size = self.coverImageView.bounds.size;
    
    for (UIView* labelView in self.itemLabelArray) {
        [labelView removeFromSuperview];
    }
    [self.itemLabelArray removeAllObjects];
    
    NSArray* itemArray = [QSShowUtil getAllItemArray:self.showDict];
    NSArray* itemRects = [QSShowUtil getItemRects:self.showDict];
    for (int i = 0; i < itemArray.count; i++) {
        if (i >= itemRects.count) {
            break;
        }
        NSDictionary* itemDict = itemArray[i];
        NSArray* rects = itemRects[i];
        
        QSItemTagView* labelView = [QSItemTagView generateView];
        labelView.hidden = !fShowLabel;
        [self.itemLabelArray addObject:labelView];
        labelView.userInteractionEnabled = YES;
        UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(_didTapItemLabel:)];
        

        if ([QSEntityUtil checkIsDict:itemDict] &&
            ![QSItemUtil getDelist:itemDict] &&
            rects.count == 4) {
            NSNumber* reduction = [QSItemUtil getExpectableReduction:itemDict];
            if (reduction) {
                labelView.tagLabel.text = [NSString stringWithFormat:@"%@", reduction];
            } else {
#warning TODO 改为圆圈
                labelView.tagLabel.text = [NSString stringWithFormat:@"%d", 0];
            }
            
            [labelView updateSize];
            labelView.transform = CGAffineTransformMakeScale(1.1, 1.1);
            
            CGFloat x = ((NSNumber*)rects[0]).floatValue + ((NSNumber*)rects[2]).floatValue / 2;
            CGFloat y = ((NSNumber*)rects[1]).floatValue + ((NSNumber*)rects[3]).floatValue / 2;
            x = x * size.width / 100;
            y = y * size.height / 100;
            labelView.center = CGPointMake(origin.x + x, origin.y + y);
            [labelView addGestureRecognizer:ges];
            [self.coverLabelContainerView addSubview:labelView];
        }
        
    }
}

- (void)_didTapItemLabel:(UITapGestureRecognizer*)ges {
    UIView* itemLabelView = ges.view;
    NSUInteger index = [self.itemLabelArray indexOfObject:itemLabelView];
    if (index != NSNotFound) {
        NSArray* itemArray = [QSShowUtil getAllItemArray:self.showDict];
        NSDictionary* itemDict = itemArray[index];
        NSDictionary *people = [QSShowUtil getPeopleFromShow:self.showDict];
        NSString *peopleId = [QSPeopleUtil getPeopleId:people];
        if (itemDict) {
            UIViewController* vc = [[QSS11ItemBuyViewController alloc] initWithItem:itemDict promoterId:peopleId];
            [self.navigationController pushViewController:vc animated:YES];
        }
    }
}

- (void)bindExceptImageWithDict:(NSDictionary*)dict
{
    self.coverLabelContainerView.hidden = ![QSShowUtil getItemReductionEnabled:dict];
    self.videoIcon.hidden = !self.generateVideoPath;

    //Like Btn
    [self setLikeBtnHover:[QSShowUtil getIsLike:dict]];
    
    [self.commentBtn setTitle:[QSShowUtil getNumberCommentsDescription:dict] forState:UIControlStateNormal];
    [self.favorBtn setTitle:[QSShowUtil getNumberLikeDescription:dict] forState:UIControlStateNormal];
    
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:self.showDict];
    [self updatePeopleInfo:peopleDict];
    if (peopleDict && self.showDeletedBtn) {
        //当前用户
        self.releaseDateLabel.hidden = NO;
        self.trashBtn.hidden = NO;
        self.favorBtn.hidden = YES;
        self.videoIcon.hidden = YES;
        self.pauseBtn.hidden = YES;
        NSDate* createDate = [QSShowUtil getCreatedDate:dict];
        self.releaseDateLabel.text = [NSString stringWithFormat:@"发布日期：%@", [QSDateUtil buildDayStringFromDate:createDate]];
    }
    [self _updateLabel];
}
#pragma mark - getNewItemArray
- (NSString *)getItemArrayCount:(NSDictionary *)dict
{
    NSArray *itemArray = [QSShowUtil getItems:dict];
    NSMutableArray *array = [[NSMutableArray alloc]init];
    if (itemArray.count) {
        for (int i = 0; i < itemArray.count; i ++) {
            NSDictionary *dic = itemArray[i];
            if ([QSEntityUtil checkIsNil:dic[@"delist"]]) {
                [array addObject:dic];
            }
        }
    }
    return [NSString stringWithFormat:@"%@",@(array.count)];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - UI
- (void)setLikeBtnHover:(BOOL)fHover
{
    if (fHover) {
        [self.favorBtn setBackgroundImage:[UIImage imageNamed:@"s03_like_btn_full"] forState:UIControlStateNormal];
    } else {
        [self.favorBtn setBackgroundImage:[UIImage imageNamed:@"s03_like_btnIcon"] forState:UIControlStateNormal];
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
    
    NSDictionary* showDict = self.showDict;
    [SHARE_NW_ENGINE handleShowLike:showDict onSucceed:^(BOOL f) {
        if (f) {
            [self showSuccessHudWithText:@"添加收藏"];
        } else {
            [self showSuccessHudWithText:@"取消收藏"];
        }
        [self bindExceptImageWithDict:showDict];
        
    } onError:^(NSError *error) {
        [self handleError:error];
        [self bindExceptImageWithDict:showDict];
    }];
}

#pragma mark -

- (IBAction)trashBtnPressed:(id)sender {
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil message:@"确定删除？" delegate:self cancelButtonTitle:@"确定" otherButtonTitles:@"取消", nil];
    [alert show];
    
}
#pragma mark - AlertViewDelegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 0) {
        [SHARE_NW_ENGINE matcherHide:self.showDict onSucceed:^{
            [self showTextHud:@"删除成功"];
            [self performSelector:@selector(backBtnPressed:) withObject:nil afterDelay:TEXT_HUD_DELAY];
        } onError:^(NSError *error) {
            [self handleError:error];
        }];
    }
    else
    {
        [self showTextHud:@"取消删除"];
    }
}

-(void)playMovie:(NSString *)path{
    [self hideSharePanel];
    [super playMovie:path];
}

#pragma mark - Share
- (void)showSharePanel
{
    NSString* showId = [QSEntityUtil getIdOrEmptyStr:self.showDict];
    
    //获取userID
   // QSUserManager *um = [QSUserManager shareUserManager];
   // NSString* peopleID = [QSEntityUtil getIdOrEmptyStr:um.userInfo];
    __weak QSS03ShowDetailViewController *weakSelf = self;
    [SHARE_NW_ENGINE shareCreateShow:showId onSucceed:^(NSDictionary *shareDic) {
        if (shareDic) {
            [weakSelf.shareVc showSharePanelWithTitle:[QSShareUtil getShareTitle:shareDic] desc:[QSShareUtil getShareDesc:shareDic] url:[QSShareUtil getshareUrl:shareDic] shareIconUrl:[QSShareUtil getShareIcon:shareDic]];
        }
       
    } onError:^(NSError *error) {
        
    }];
    
    
}
- (void)hideSharePanel
{
    [self.shareVc hideSharePanel];
}


#pragma mark - Override
#pragma mark Data
- (NSArray*)generateImagesData
{
    return [QSShowUtil getShowVideoPreviewUrlArray:self.showDict];;
}
- (NSString*)generateVideoPath
{
    return [QSShowUtil getVideoPath:self.showDict];
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
//    self.playBtn.hidden = hidden;
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
    [MobClick event:@"playVideo" attributes:@{@"showId" : [QSEntityUtil getIdOrEmptyStr:self.showDict], @"length": @(playbackTime).stringValue} durations:(int)(playbackTime * 1000)];
}

#pragma mark - Discount
- (void)hideDiscountContainer {
    __weak QSS03ShowDetailViewController* weakSelf = self;
    [UIView animateWithDuration:.5f animations:^{
        weakSelf.discountContainer.alpha = 0;
    } completion:^(BOOL finished) {
        weakSelf.discountContainer.hidden = YES;
    }];

}
- (void)didTapHeadIcon:(id)sender {
    NSDictionary* peopleDict = [QSShowUtil getPeopleFromShow:self.showDict];
    if (peopleDict) {
        QSU01UserDetailViewController *vc = [[QSU01UserDetailViewController alloc]initWithPeople:peopleDict];
        [self.navigationController pushViewController:vc animated:YES];
    }
}


- (void)showDiscountContainer{
    self.discountContainer.hidden = NO;
    self.discountContainer.alpha = 1.f;
    [self performSelector:@selector(hideDiscountContainer) withObject:nil afterDelay:5.f];
}

- (void)didTapLabelContainer:(UITapGestureRecognizer*)tap {
    self.showShouldLabel = !self.showShouldLabel;
    for (UIView* v  in self.itemLabelArray) {
        v.hidden = !self.showShouldLabel;
    }
    
    if (!self.showShouldLabel && [self generateVideoPath]) {
        [self playOrPauseBtnPressed:nil];
    }
    
}


- (void)imageScrollViewDidTapImgView:(QSImageScrollViewBase *)view {
    [self playOrPauseBtnPressed:nil];
}
- (void)pauseVideo {
    [super pauseVideo];
    [self didTapLabelContainer:nil];
}
@end
