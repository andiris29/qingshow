//
//  QSS03ShopDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 12/16/14.
//  Copyright (c) 2014 QS. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "QSS03ItemShopDetailViewController.h"

@interface QSS03ItemShopDetailViewController ()

@property (strong, nonatomic) NSDictionary* showDict;
@property (assign, nonatomic) int currentItemIndex;

@end

@implementation QSS03ItemShopDetailViewController

#pragma mark - Init
- (id)initWithShow:(NSDictionary*)showDict currentItemIndex:(int)index
{
    self = [super initWithNibName:@"QSS03ItemShopDetailViewController" bundle:nil];
    if (self) {
        self.showDict = showDict;
        self.currentItemIndex = index;
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.iconImageView.layer.cornerRadius = self.iconImageView.frame.size.height / 2;
    self.iconImageView.layer.masksToBounds = YES;
    self.showBtn.layer.cornerRadius = 8;
    self.showBtn.layer.masksToBounds = YES;
//    [self configScrollView];
//    [self config];
//    self.scrollView.contentInset = UIEdgeInsetsMake(0, 0, 0, 0);
}
- (void)viewDidLayoutSubviews
{
    [super viewDidLayoutSubviews];
    self.scrollView.contentInset = UIEdgeInsetsZero;
}
- (void)config
{
    UIView* view0 = [[UIView alloc] init];
    view0.backgroundColor = [UIColor redColor];
    view0.translatesAutoresizingMaskIntoConstraints = NO;
    UIView* view1 = [[UIView alloc] init];
    view1.backgroundColor = [UIColor grayColor];
    view1.translatesAutoresizingMaskIntoConstraints = NO;
    UIView* view2 = [[UIView alloc] init];
    view2.backgroundColor = [UIColor blueColor];
    view2.translatesAutoresizingMaskIntoConstraints = NO;
    view0.frame = CGRectMake(0, 0, 300, 300);
    view1.frame = CGRectMake(0, 300, 300, 300);
    view2.frame = CGRectMake(0, 600, 300, 300);
    [self.scrollView addSubview:view0];
    [self.scrollView addSubview:view1];
    [self.scrollView addSubview:view2];
    self.scrollView.contentSize = CGSizeMake(300, 900);
}
- (void)configScrollView
{
//    self.scrollView.translatesAutoresizingMaskIntoConstraints = NO;
    UIView* view0 = [[UIView alloc] init];
    view0.backgroundColor = [UIColor redColor];
    view0.translatesAutoresizingMaskIntoConstraints = NO;
    UIView* view1 = [[UIView alloc] init];
    view1.backgroundColor = [UIColor grayColor];
    view1.translatesAutoresizingMaskIntoConstraints = NO;
    UIView* view2 = [[UIView alloc] init];
    view2.backgroundColor = [UIColor blueColor];
    view2.translatesAutoresizingMaskIntoConstraints = NO;
    CGSize screenSize = [UIScreen mainScreen].bounds.size;
    
    [self.scrollView addSubview:view0];
    [self.scrollView addSubview:view1];
    [self.scrollView addSubview:view2];
    NSDictionary* viewDict = NSDictionaryOfVariableBindings(view0, view1, view2);
    NSArray* constraints = [NSLayoutConstraint constraintsWithVisualFormat:@"V:|[view0(winHeight)][view1(winHeight)][view2(winHeight)]|" options:0 metrics:@{@"winHeight" : @(screenSize.height)} views:viewDict];
    [self.scrollView addConstraints:constraints];
    
    for (UIView* v in @[view0, view1, view2]) {
        NSArray* constraints = [NSLayoutConstraint constraintsWithVisualFormat:@"H:|-0-[view(winWidth)]-0-|" options:0 metrics:@{@"winWidth" : @(screenSize.width)} views:@{@"view" : v}];
        [self.scrollView addConstraints:constraints];
    }
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
#pragma mark - IBAction
- (IBAction)shopButtonPressed:(id)sender {
    
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    NSLog(@"%f",scrollView.contentOffset.y);
}
@end
