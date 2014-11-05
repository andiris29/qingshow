//
//  QSModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSModelDetailViewController.h"
#import "QSModelBadgeView.h"

@interface QSModelDetailViewController ()

@property (strong, nonatomic) NSDictionary* peopleDict;

@end

@implementation QSModelDetailViewController

#pragma mark - Init
- (id)initWithModel:(NSDictionary*)peopleDict
{
    self = [self initWithNibName:@"QSModelDetailViewController" bundle:nil];
    if (self)
    {
        self.peopleDict = peopleDict;
    }
    return self;
}
#pragma mark - View
- (void)configView
{
    QSModelBadgeView* badgeView = [QSModelBadgeView generateView];
    [self.badgeContainer addSubview:badgeView];
}


#pragma mark - Network



#pragma mark - Life Cycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self configView];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
