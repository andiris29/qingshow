//
//  QSActivityViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 15/12/3.
//  Copyright © 2015年 QS. All rights reserved.
//

#import "QSActivityViewController.h"
#import "UIImageView+MKNetworkKitAdditions.h"

@interface QSActivityViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *imageView;

@end

@implementation QSActivityViewController

#pragma mark - Init
- (instancetype)initWithImgPath:(NSString*)path {
    self = [super initWithNibName:@"QSActivityViewController" bundle:nil];
    if (self) {
        self.path = path;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    self.imageView.userInteractionEnabled = YES;
    UITapGestureRecognizer* ges = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapImage:)];
    [self.imageView addGestureRecognizer:ges];
    [self.imageView setImageFromURL:[NSURL URLWithString:self.path]];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark -
- (void)didTapImage:(UITapGestureRecognizer*)ges {
    if ([self.delegate respondsToSelector:@selector(activityVcShouldDismiss:)]) {
        [self.delegate activityVcShouldDismiss:self];
    }
}
@end
