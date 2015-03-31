//
//  QSS13TopicDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 4/1/15.
//  Copyright (c) 2015 QS. All rights reserved.
//

#import "QSS13TopicDetailViewController.h"

@interface QSS13TopicDetailViewController ()
@property (strong, nonatomic) NSDictionary* topicDict;
@end

@implementation QSS13TopicDetailViewController
#pragma mark - Init
- (id)initWithTopic:(NSDictionary*)topicDict
{
    self = [super initWithNibName:@"QSS13TopicDetailViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}

#pragma mark - Life Cycle
- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    if ([self respondsToSelector:@selector(setAutomaticallyAdjustsScrollViewInsets:)]) {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
    self.collectionView.contentInset = UIEdgeInsetsMake(200.f, 0, 0, 0);

}
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - IBAction

- (IBAction)backBtnPressed:(id)sender {
}
@end
