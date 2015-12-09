//
//  QSG02WelcomeViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 5/8/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSG02WelcomeViewController.h"
#import "UIView+QSExtension.h"
#define PAGE_ID @"G02 - 欢迎页"

#define w ([UIScreen mainScreen].bounds.size.width)
#define h ([UIScreen mainScreen].bounds.size.height)
@interface QSG02WelcomeViewController ()

@end

@implementation QSG02WelcomeViewController
- (instancetype)init
{
    self = [super initWithNibName:@"QSG02WelcomeViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.view.frame = [UIScreen mainScreen].bounds;
    [self.navigationController.navigationBar setTitleTextAttributes:
     
     @{NSFontAttributeName:NAVNEWFONT,
       
       NSForegroundColorAttributeName:[UIColor blackColor]}];
    self.pageControl.currentPageIndicatorTintColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"page_select"]];
    
    
    _welcomeSCV.frame = CGRectMake(0, 0, w, h);
    _welcomeSCV.contentSize = CGSizeMake(w*4, h);
    _welcomeSCV.pagingEnabled = YES;
    _welcomeSCV.alpha = 1.0f;
    _welcomeSCV.delegate = self;
    _welcomeSCV.bounces = NO;
    _welcomeSCV.alwaysBounceHorizontal = YES;
    _welcomeSCV.showsHorizontalScrollIndicator = NO;
    [self addPhotosToSVC];
    
    
    _pageControl.numberOfPages = 3;
    _pageControl.currentPage = 0;
    _pageControl.userInteractionEnabled = NO;
   // _pageControl.transform = CGAffineTransformMakeScale(1.3, 1.3);
}

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    [MobClick beginLogPageView:PAGE_ID];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [MobClick endLogPageView:PAGE_ID];
}

- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    _pageControl.center = CGPointMake(w / 2, h / 10 * 8.5);
}

- (void)addPhotosToSVC
{
    UIImage *img01 = nil;
    UIImage *img02 = nil;
    UIImage *img03 = nil;
    unsigned int  num = 0;
    if (self.view.bounds.size.width == 320) {
        if (self.view.bounds.size.height == 568) {
            num = 568;
        }
        else{
             num = 480;
            
        }
    }
    else if(self.view.bounds.size.width == 375){
        num = 667;
    }
    else if(self.view.bounds.size.width == 414)
    {
        num = 736;
    }
    //NSLog(@"w = %f,h = %f",w,h);
    
    NSString *imgName01 = [NSString stringWithFormat:@"/welcome1_%d.png",num];
    NSString *imgName02 = [NSString stringWithFormat:@"/welcome2_%d.png",num];
    NSString *imgName03 = [NSString stringWithFormat:@"/welcome3_%d.png",num];
//缓存优化
    NSString *path01 = [[[NSBundle mainBundle]bundlePath] stringByAppendingString:imgName01];
    NSString *path02 = [[[NSBundle mainBundle]bundlePath] stringByAppendingString:imgName02];
    NSString *path03 = [[[NSBundle mainBundle]bundlePath] stringByAppendingString:imgName03];
    
    img01 = [UIImage imageWithContentsOfFile:path01];
    img02 = [UIImage imageWithContentsOfFile:path02];
    img03 = [UIImage imageWithContentsOfFile:path03];
   
    
    for (int i = 0;  i < 4; i ++) {
        UIImageView *imageView = [[UIImageView alloc]initWithFrame:CGRectMake(w*i, 0, w, h)];
        [_welcomeSCV addSubview:imageView];
        if (i == 0) {
            imageView.image = img01;
        }
        else if(i == 1)
        {
            imageView.image = img02;
        }
        else if(i == 2)
        {
            imageView.image = img03;
        }
        else if(i == 3){
            UIView *view = [[UIView alloc]initWithFrame:CGRectMake(w*3, 0, w, h)];
            view.backgroundColor = [UIColor clearColor];
            view.alpha = .8f;
            [_welcomeSCV addSubview:view];
        }
    }

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#define ISFIRSTLOGIN @"isFirstLogin"
#pragma mark - IBAction
- (IBAction)skipBtnPressed:(id)sender {
    if ([self.delegate respondsToSelector:@selector(dismissWelcomePage:)]) {
        [self.delegate dismissWelcomePage:self];
    }
}
#pragma mark - UIScrollView 代理
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    int pageNum =  _welcomeSCV.contentOffset.x/w;
    _pageControl.currentPage = pageNum;
    //NSLog(@"offset  =  %f",_welcomeSCV.contentOffset.x);
    if (_welcomeSCV.contentOffset.x == w*3) {
        [self skipBtnPressed:self];
    }
}

#pragma mark - Third Part


@end
