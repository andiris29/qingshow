//
//  QSImageEditingViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSImageEditingViewController.h"

@interface QSImageEditingViewController ()

@property (assign, nonatomic) QSImageEditingViewControllerType type;
@property (strong, nonatomic) UIImage* img;
@end

@implementation QSImageEditingViewController
- (id)initWithType:(QSImageEditingViewControllerType)type image:(UIImage*)img;
{
    self = [self initWithNibName:@"QSImageEditingViewController" bundle:nil];
    if (self) {
        self.type = type;
        self.img = img;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    float h = [UIScreen mainScreen].bounds.size.width;
    if (self.type == QSImageEditingViewControllerTypeBg) {
        self.highCon.constant = h * 228 / 320;
    } else {
        self.highCon.constant = h;
    }
    self.cropperView.layer.borderWidth = 1.f;
    self.cropperView.layer.borderColor = [UIColor whiteColor].CGColor;
    [self.cropperView setup];
    
    self.cropperView.image = self.img;
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)cancelBtnPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(cancelImageEditing:)]) {
        [self.delegate cancelImageEditing:self];
    }
}

- (IBAction)useBtnPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(imageEditingUseImage:vc:)]) {
        [self.cropperView finishCropping];
        [self.delegate imageEditingUseImage:self.cropperView.croppedImage vc:self];
    }
}
@end
