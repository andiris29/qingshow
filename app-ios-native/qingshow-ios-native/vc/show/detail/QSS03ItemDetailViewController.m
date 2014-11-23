//
//  QSS03ItemDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ItemDetailViewController.h"
#import "UIViewController+ShowHud.h"
#import "UIImageView+MKNetworkKitAdditions.h"
#import "QSItemUtil.h"

@interface QSS03ItemDetailViewController ()

@property (strong, nonatomic) NSDictionary* itemDict;

@end

@implementation QSS03ItemDetailViewController

- (id)initWithItemDict:(NSDictionary*)itemDict
{
    self = [self initWithNibName:@"QSS03ItemDetailViewController" bundle:nil];
    if (self) {
        self.itemDict = itemDict;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self.imageView setImageFromURL:[QSItemUtil getCoverUrl:self.itemDict]];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction
- (IBAction)closeBtnPressed:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)brandBtnPressed:(id)sender {
    
}
- (IBAction)shopBtnPressed:(id)sender {
    NSURL* showUrl = [QSItemUtil getShopUrl:self.itemDict];
    if (showUrl) {
        [[UIApplication sharedApplication] openURL:showUrl];
    } else {
        [self showErrorHudWithText:@"暂无地址"];
    }

}
@end
