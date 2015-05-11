//
//  QSS14PreviewDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/11/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS14PreviewDetailViewController.h"

@interface QSS14PreviewDetailViewController ()

@end

@implementation QSS14PreviewDetailViewController
#pragma mark - Init
- (instancetype)initWithPreview:(NSDictionary*)previewDict
{
    self = [super initWithNibName:@"QSS14PreviewDetailViewController" bundle:nil];
    if (self) {
        
    }
    
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Rotation
-(BOOL)shouldAutorotate {
    return YES;
}
-(NSUInteger)supportedInterfaceOrientations {
    return UIInterfaceOrientationMaskLandscape;
}
- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return UIInterfaceOrientationLandscapeLeft;
}
@end
