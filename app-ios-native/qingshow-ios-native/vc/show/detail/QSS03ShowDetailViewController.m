//
//  QSS03ShowDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/10/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSS03ShowDetailViewController.h"
#import "QSSingleImageScrollView.h"
#import "QSItemImageScrollView.h"

@interface QSS03ShowDetailViewController ()

@property (strong, nonatomic) QSSingleImageScrollView* showImageScrollView;

@end

@implementation QSS03ShowDetailViewController
#pragma mark - Init Method
- (IBAction)favorBtnPressed:(id)sender {
}

- (id)init
{
    self = [self initWithNibName:@"QSS03ShowDetailViewController" bundle:nil];
    if (self) {
        
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
#warning 样例图片
    NSArray* urlArray = @[[NSURL URLWithString:@"http://d.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=b61e1813940a304e4d22adfae1f3c4f4/9358d109b3de9c829a5995986f81800a19d843ec.jpg"],
                          [NSURL URLWithString:@"http://f.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=1c9ba895d3a20cf45990f3df46322844/342ac65c10385343beeba8339013b07eca8088ff.jpg"],
                          [NSURL URLWithString:@"http://e.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=8603fc8f0ff431ada3d24e397b0dcfdd/c75c10385343fbf286a5bf3eb37eca8065388f25.jpg"]];
    self.showImageScrollView.imageUrlArray = urlArray;
    
    QSItemImageScrollView* s = [[QSItemImageScrollView alloc] initWithFrame:CGRectMake(0, 0, 300, 120)];
    [self.itemContainer addSubview:s];
    urlArray = @[[NSURL URLWithString:@"http://d.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=b61e1813940a304e4d22adfae1f3c4f4/9358d109b3de9c829a5995986f81800a19d843ec.jpg"],
                          [NSURL URLWithString:@"http://f.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=1c9ba895d3a20cf45990f3df46322844/342ac65c10385343beeba8339013b07eca8088ff.jpg"],
                          [NSURL URLWithString:@"http://e.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=8603fc8f0ff431ada3d24e397b0dcfdd/c75c10385343fbf286a5bf3eb37eca8065388f25.jpg"],
                          [NSURL URLWithString:@"http://e.hiphotos.baidu.com/image/h%3D800%3Bcrop%3D0%2C0%2C1280%2C800/sign=8603fc8f0ff431ada3d24e397b0dcfdd/c75c10385343fbf286a5bf3eb37eca8065388f25.jpg"]];
    s.imageUrlArray = urlArray;
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
    NSLog(@"commentBtnPressed");
}

- (IBAction)shareBtnPressed:(id)sender {
    NSLog(@"shareBtnPressed");
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
