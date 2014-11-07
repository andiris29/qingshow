//
//  QSModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP02ModelDetailViewController.h"
#import "QSModelBadgeView.h"

@interface QSP02ModelDetailViewController ()

@property (strong, nonatomic) QSModelBadgeView* badgeView;

#pragma mark - Data
@property (strong, nonatomic) NSDictionary* peopleDict;
@property (assign, nonatomic) QSModelSection currentSection;
@property (strong, nonatomic) NSMutableArray* showsArray;
@property (strong, nonatomic) NSMutableArray* followingArray;
@property (strong, nonatomic) NSMutableArray* followerArray;

@end

@implementation QSP02ModelDetailViewController

#pragma mark - Init
- (id)initWithModel:(NSDictionary*)peopleDict
{
    self = [self initWithNibName:@"QSP02ModelDetailViewController" bundle:nil];
    if (self)
    {
        self.peopleDict = peopleDict;
    }
    return self;
}
#pragma mark - View
- (void)configView
{
    //badge view
    self.badgeView = [QSModelBadgeView generateView];
    [self.badgeContainer addSubview:self.badgeView];
    
    //table view
    
    //collectioin view
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


#pragma mark - QSModelBadgeViewDelegate
- (void)changeToSection:(QSModelSection)section
{
    
}
- (void)followButtonPressed
{
    
}
#pragma mark - Table View


#pragma mark - Colletion View


@end
