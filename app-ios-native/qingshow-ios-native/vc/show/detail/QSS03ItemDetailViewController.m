//
//  QSS03ItemDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/11/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ItemDetailViewController.h"
#import "UIImageView+MKNetworkKitAdditions.h"

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
    NSURL* sampleUrl = [NSURL URLWithString:@"http://e.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=8603fc8f0ff431ada3d24e397b0dcfdd/c75c10385343fbf286a5bf3eb37eca8065388f25.jpg"];
    [self.imageView setImageFromURL:sampleUrl];
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
    
}
@end
