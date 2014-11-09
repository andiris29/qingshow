//
//  QSModelDetailViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 11/3/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSP02ModelDetailViewController.h"
#import "QSModelBadgeView.h"
#import "QSNetworkEngine.h"

@interface QSP02ModelDetailViewController ()

@property (strong, nonatomic) QSModelBadgeView* badgeView;

#pragma mark - Data
@property (strong, nonatomic) NSDictionary* peopleDict;
@property (assign, nonatomic) QSModelSection currentSection;
@property (strong, nonatomic) NSMutableArray* showsArray;
@property (strong, nonatomic) NSMutableArray* followingArray;
@property (strong, nonatomic) NSMutableArray* followerArray;



@property (assign, nonatomic) CGPoint touchLocation;
@property (strong, nonatomic) UIView* currentTouchView;


#pragma mark - Delegate Obj
@property (strong, nonatomic) QSShowWaterfallDelegateObj* showsDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followingDelegate;
@property (strong, nonatomic) QSModelListTableViewDelegateObj* followerDelegate;
@end

@implementation QSP02ModelDetailViewController

#pragma mark - Init
- (id)initWithModel:(NSDictionary*)peopleDict
{
    self = [self initWithNibName:@"QSP02ModelDetailViewController" bundle:nil];
    if (self)
    {
        self.peopleDict = peopleDict;
        
        [self delegateObjInit];
    }
    return self;
}
- (void)delegateObjInit
{
    self.showsDelegate = [[QSShowWaterfallDelegateObj alloc] init];
    self.followingDelegate = [[QSModelListTableViewDelegateObj alloc] init];
    self.followerDelegate = [[QSModelListTableViewDelegateObj alloc] init];
    
    
}

#pragma mark - View
- (void)configView
{
    //title
    self.title = self.peopleDict[@"name"];
    //badge view
    self.badgeView = [QSModelBadgeView generateView];
    [self.badgeContainer addSubview:self.badgeView];
    
    //following table view
    
    
    //follower table view
    
    
    //Show collectioin view
    [self.showsDelegate bindWithCollectionView:self.showCollectionView];
    __weak QSP02ModelDetailViewController* weakSelf = self;
    
    self.showsDelegate.networkBlock = ^MKNetworkOperation*(ArraySuccessBlock succeedBlock, ErrorBlock errorBlock, int page){
        return [SHARE_NW_ENGINE getFeedByModel:weakSelf.peopleDict[@"_id"] page:page onSucceed:succeedBlock onError:errorBlock];
//        return [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:succeedBlock onError:errorBlock];
    };
    self.showsDelegate.delegate = self;
    [self.showsDelegate fetchDataOfPage:1];
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


#pragma mark - 
- (IBAction)scrollGestureHandler:(UIGestureRecognizer*)ges
{
    
    if (ges.state == UIGestureRecognizerStateBegan) {
        self.currentTouchView = ges.view;
        self.touchLocation = [ges locationInView:self.currentTouchView];
    } else if (ges.state == UIGestureRecognizerStateChanged) {
        if (self.currentTouchView == ges.view) {
            CGPoint newLoc = [ges locationInView:self.currentTouchView];
            self.topConstrain.constant += [ges locationInView:self.currentTouchView].y - self.touchLocation.y;
            [self.view layoutIfNeeded];
        }
    } else {
        NSLog(@"end");
    }
}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
//    NSLog(@"begin");
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
//    NSLog(@"move");
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
//    NSLog(@"end");
}
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
  
    
    if (self.currentTouchView != scrollView) {
        self.currentTouchView = scrollView;
        self.touchLocation = scrollView.contentOffset;
    }
    else
    {
        self.topConstrain.constant += self.touchLocation.y - scrollView.contentOffset.y;
        BOOL f = YES;
        if (self.topConstrain.constant > 0) {
            self.topConstrain.constant = 0;
            f = NO;
        }
        if (self.topConstrain.constant < -115) {
            self.topConstrain.constant = -115;
            f = NO;
        }
        if (f) {
            scrollView.contentOffset = self.touchLocation;
        }

        [self.view layoutIfNeeded];
        
    }


}

@end
