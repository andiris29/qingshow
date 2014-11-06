//
//  QSViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSRootViewController.h"

#import "QSTimeCollectionViewCell.h"
#import "QSNetworkEngine.h"
#import "QSModelListViewController.h"
#import "QSUserSettingViewController.h"

@interface QSRootViewController ()

@property (strong, nonatomic) NSMutableArray* resultArray;
@property (assign, nonatomic) int currentPage;
@property (strong, nonatomic) QSRootMenuView* menuView;
@property (assign, nonatomic) BOOL fIsShowMenu;
@end

@implementation QSRootViewController
#pragma mark - 
- (id)init
{
    self = [self initWithNibName:@"QSRootViewController" bundle:nil];
    if (self) {
        
    }
    return self;
}
#pragma mark - Life Cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.MyLayout *layout=[[MyLayout alloc]init];
    self.resultArray = [@[] mutableCopy];
    self.currentPage = 1;
    [self fetchDataOfPage:1];
    
    [self configNavBar];
    [self configCollectionView];
    QSRootMenuView* menuView = [QSRootMenuView generateView];
    [self.menuContainer addSubview:menuView];
    self.menuView = menuView;
    self.fIsShowMenu = NO;
    menuView.delegate = self;
    
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@" " style:UIBarButtonItemStyleDone target:nil action:nil];
    [[self navigationItem] setBackBarButtonItem:backButton];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Network
- (void)fetchDataOfPage:(int)page
{
    MKNetworkOperation* op = [SHARE_NW_ENGINE getChosenFeedingPage:page onSucceed:^(NSArray *showArray, NSDictionary *metadata) {
        if (page == 1) {
            [self.resultArray removeAllObjects];
        }
        [self.resultArray addObjectsFromArray:showArray];
        [self.collectionView reloadData];
    } onError:^(NSError *error) {
        NSLog(@"error");
    }];
}



#pragma mark - Configure View
- (void)configNavBar
{
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    
    UIBarButtonItem* menuItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu"] style:UIBarButtonItemStylePlain target:self action:@selector(menuButtonPressed)];
    self.navigationItem.leftBarButtonItem = menuItem;
    UIBarButtonItem* accountItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_account"] style:UIBarButtonItemStylePlain target:self action:@selector(accountButtonPressed)];
    self.navigationItem.rightBarButtonItem = accountItem;
}
- (void)configCollectionView
{
    QSWaterFallCollectionViewLayout* layout = [[QSWaterFallCollectionViewLayout alloc] init];
    self.collectionView.collectionViewLayout = layout;
    self.collectionView.translatesAutoresizingMaskIntoConstraints = NO;
    
    self.collectionView.scrollEnabled=YES;
    self.collectionView.backgroundColor=[UIColor colorWithRed:240.f/255.f green:240.f/255.f blue:240.f/255.f alpha:1.f];
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSWaterFallCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSWaterFallCollectionViewCell"];
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSTimeCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:@"QSTimeCollectionViewCell"];
}

#pragma mark - IBAction
- (void)menuButtonPressed
{
    __weak QSRootViewController* weakSelf = self;
    if (self.fIsShowMenu)
    {
        [self.menuView hideMenuAnimationComple:^{
            weakSelf.menuContainer.hidden = YES;
        }];
    }
    else
    {
        weakSelf.menuContainer.hidden = NO;
        [self.menuView showMenuAnimationComple:^{
        }];
    }
    self.fIsShowMenu = !self.fIsShowMenu;
    

}
- (void)accountButtonPressed
{
    NSLog(@"accountBtnPressed");
    QSUserSettingViewController *vc = [[QSUserSettingViewController alloc]
                                       initWithNibName:@"QSUserSettingViewController"
                                                bundle:nil];
    
    [self presentViewController:vc
                       animated:YES
                     completion:nil];
}
#pragma -mark UICollectionView delegate
- (NSDictionary*)getShowDictForIndexPath:(NSIndexPath*)indexPath
{
    return self.resultArray[indexPath.row - 1];
}

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        return CGSizeMake(145, 35);
    } else {
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        float height = [QSWaterFallCollectionViewCell getHeightWithData:dict];
        return CGSizeMake(145, height);
    }

}

//margin
-(UIEdgeInsets)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout *)collectionViewLayout insetForSectionAtIndex:(NSInteger)section
{
    return UIEdgeInsetsMake(10, 10, 10, 10);
}

-(void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath
{
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return self.resultArray.count + 1;
}


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        QSTimeCollectionViewCell* cell = (QSTimeCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSTimeCollectionViewCell" forIndexPath:indexPath];
        return cell;
    } else {
        QSWaterFallCollectionViewCell* cell = (QSWaterFallCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSWaterFallCollectionViewCell" forIndexPath:indexPath];
        cell.delegate = self;
        NSDictionary* dict = [self getShowDictForIndexPath:indexPath];
        [cell bindData:dict];
        
        return cell;
    }
}

#pragma mark - QSWaterFallCollectionViewCellDelegate
- (void)favorBtnPressed:(QSWaterFallCollectionViewCell*)cell
{
    
    
}
#pragma mark - QSRootMenuViewDelegate
- (void)rootMenuItemPressedType:(int)type
{
    UIViewController* vc = [[QSModelListViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}

@end
