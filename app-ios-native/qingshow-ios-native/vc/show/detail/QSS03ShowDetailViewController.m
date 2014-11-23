//
//  QSS03ShowDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ShowDetailViewController.h"
#import "QSSingleImageScrollView.h"

#import "QSS03ItemDetailViewController.h"
#import "QSCommentListViewController.h"
#import "QSShowUtil.h"
#import "QSModelUtil.h"
#import "UIImageView+MKNetworkKitAdditions.h"


@interface QSS03ShowDetailViewController ()

@property (strong, nonatomic) QSSingleImageScrollView* showImageScrollView;
@property (strong, nonatomic) QSItemImageScrollView* itemImageScrollView;

@property (strong, nonatomic) NSDictionary* showDict;

@end

@implementation QSS03ShowDetailViewController
#pragma mark - Init Method
- (IBAction)favorBtnPressed:(id)sender {
}

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
}

- (void)bindWithDict:(NSDictionary*)dict
{
    //Model
    NSDictionary* peopleInfo = dict[@"modelRef"];
    
    NSString* iconPath = peopleInfo[@"portrait"];
    NSURL* iconUrl = [NSURL URLWithString:iconPath];
    [self.headIconImageView setImageFromURL:iconUrl];
    
    self.nameLabel.text = peopleInfo[@"name"];
    self.detailLabel.text = [QSModelUtil buildModelStatusString:peopleInfo];
    NSDictionary* modelInfo = peopleInfo[@"modelInfo"];
    NSString* status = nil;
    if (modelInfo) {
        status = modelInfo[@"status"];
        status = status ? status : @"";
    }
    self.contentLabel.text = status;
    self.favorNumberLabel.text = [QSModelUtil buildNumLikeString:peopleInfo];
    
    //Image
    NSArray* previewArray = [QSShowUtil getShowVideoPreviewUrlArray:dict];
    self.showImageScrollView.imageUrlArray = previewArray;
    NSArray* itemUrlArray = [QSShowUtil getItemsImageUrlArrayFromShow:dict];
    self.itemImageScrollView.imageUrlArray = itemUrlArray;

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)playBtnPressed:(id)sender {
    NSLog(@"playBtnPressed");
}

- (IBAction)commentBtnPressed:(id)sender {
    UIViewController* vc =[[QSCommentListViewController alloc] initWithShow:self.showDict];
    [self.navigationController pushViewController:vc animated:YES];
}

- (IBAction)shareBtnPressed:(id)sender {
#warning 暂时显示item detail
    UIViewController* vc = [[QSS03ItemDetailViewController alloc] init];
    [self presentViewController:vc animated:YES completion:nil];
}


#pragma mark - QSItemImageScrollViewDelegate
- (void)didTapItemAtIndex:(int)index
{
    NSDictionary* itemDict = [QSShowUtil getItemFromShow:self.showDict AtIndex:index];
    UIViewController* vc = [[QSS03ItemDetailViewController alloc] initWithItemDict:itemDict];
    [self presentViewController:vc animated:YES completion:nil];
}
@end
