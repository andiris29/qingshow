//
//  QSS14PreviewDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS14PreviewDetailViewController.h"
#import "QSPreviewUtil.h"
#import "QSShareViewController.h"
#import "QSNetworkKit.h"
#import "UIViewController+QSExtension.h"
#import "QSShareViewController.h"


@interface QSS14PreviewDetailViewController ()

@property (strong, nonatomic) NSDictionary* previewDict;
@property (strong, nonatomic) NSArray* imagesArray;
@property (strong, nonatomic) NSString* videoPath;
@property (strong, nonatomic) QSShareViewController* shareVc;

@property (assign, nonatomic) UIInterfaceOrientationMask supportOrientation;
@end

@implementation QSS14PreviewDetailViewController
#pragma mark - Init
- (instancetype)initWithPreview:(NSDictionary*)previewDict
{
    self = [super initWithNibName:@"QSS14PreviewDetailViewController" bundle:nil];
    if (self) {
        self.previewDict = previewDict;
        NSDictionary* metadata = [QSPreviewUtil getPosterMetadata:previewDict];
        if (metadata) {
            NSNumber* width = metadata[@"width"];
            NSNumber* height = metadata[@"height"];
            if (width.intValue > height.intValue) {
                self.supportOrientation = UIInterfaceOrientationMaskLandscape;
            } else {
                self.supportOrientation = UIInterfaceOrientationMaskPortrait;
            }
        } else {
            self.supportOrientation = UIInterfaceOrientationMaskPortrait;
        }
        
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self bindWithDict:self.previewDict];
    self.fNeedScreenShot = NO;
    self.shareVc = [[QSShareViewController alloc] init];
    [self.view addSubview:self.shareVc.view];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    self.shareVc.view.frame = self.view.bounds;
}

#pragma mark - Binding
- (void)bindWithDict:(NSDictionary*)dict {
    [self bindWithDictWithoutImagesAndVideo:dict];
    self.videoPath = [QSPreviewUtil getVideoPath:dict];
    if (self.videoPath) {
        self.playBtn.hidden = NO;
#warning 暂时没有poster数据，使用first image
        //        self.imagesArray = @[[QSPreviewUtil getPosterUrl:dict]];
        self.imagesArray = @[[QSPreviewUtil getFirstImageUrl:dict]];

    } else {
        self.playBtn.hidden = YES;
        self.imagesArray = [QSPreviewUtil getImagesUrl:dict];
    }
    [self updateShowImgScrollView];
}
- (void)bindWithDictWithoutImagesAndVideo:(NSDictionary*)dict {
    [self setLikeBtnHover:[QSPreviewUtil getIsLike:dict]];
    [self.likeBtn setTitle:[QSPreviewUtil getNumLikeDesc:dict] forState:UIControlStateNormal];
}

#pragma mark - Rotation
-(BOOL)shouldAutorotate {
    return YES;
}
-(NSUInteger)supportedInterfaceOrientations {
    return self.supportOrientation;
}

- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    if (self.supportOrientation == UIInterfaceOrientationMaskPortrait) {
        return UIInterfaceOrientationPortrait;
    } else {
        return UIInterfaceOrientationLandscapeLeft;
    }
}

#pragma mark - Method To Be Override
- (NSArray*)generateImagesData
{
    return self.imagesArray;
}
- (NSString*)generateVideoPath
{
    return self.videoPath;
}

#pragma mark - IBAction
- (IBAction)shareBtnPressed:(id)sender {
    [self.shareVc showSharePanelWithUrl:nil];
}

- (IBAction)likeBtnPressed:(id)sender {
    [SHARE_NW_ENGINE handlePreviewLike:self.previewDict onSucceed:^(BOOL f) {
        [self bindWithDictWithoutImagesAndVideo:self.previewDict];
    } onError:^(NSError *error) {
        [self handleError:error];
    }];
}

#pragma mark - UI
- (void)setLikeBtnHover:(BOOL)fHover
{
    if (fHover) {
        [self.likeBtn setBackgroundImage:[UIImage imageNamed:@"s03_like_btn_full"] forState:UIControlStateNormal];
    } else {
        [self.likeBtn setBackgroundImage:[UIImage imageNamed:@"s03_like_btn"] forState:UIControlStateNormal];
    }
}
@end
