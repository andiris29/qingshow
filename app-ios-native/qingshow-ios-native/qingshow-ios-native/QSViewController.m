//
//  QSViewController.m
//  qingshow-ios-native
//
//  Created by wxy325 on 10/31/14.
//  Copyright (c) 2014 QS. All rights reserved.
//

#import "QSViewController.h"
#import "QSWaterFallCollectionViewCell.h"
#import "QSTimeCollectionViewCell.h"
@interface QSViewController ()


@end

@implementation QSViewController
#pragma mark - Life Cycle
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.MyLayout *layout=[[MyLayout alloc]init];

    [self configNavBar];
    [self configCollectionView];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
#pragma mark - Configure View
- (void)configNavBar
{
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:89.f/255.f green:86.f/255.f blue:86.f/255.f alpha:1.f];
    UIImageView* titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"nav_btn_image_logo"]];
    self.navigationItem.titleView = titleImageView;
    
    UIBarButtonItem* menuItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_menu"] style:UIBarButtonItemStylePlain target:self action:@selector(menuButtonPressed)];
    self.navigationItem.leftBarButtonItem = menuItem;
    UIBarButtonItem* accountItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"nav_btn_account"] style:UIBarButtonItemStylePlain target:self action:@selector(menuButtonPressed)];
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
    NSLog(@"menuBtnPressed");
}
- (void)accountButtonPressed
{
    NSLog(@"accountBtnPressed");
}
#pragma -mark UICollectionView delegate

- (CGSize)collectionView:(UICollectionView *)collectionView layout:(UICollectionViewLayout*)collectionViewLayout sizeForItemAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.row == 0) {
        return CGSizeMake(145, 35);
    } else {
        return CGSizeMake(145, 270);
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
    return 11;
}


-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionViews cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    UICollectionViewCell* cell = nil;
    if (indexPath.row == 0) {
        cell = (QSTimeCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSTimeCollectionViewCell" forIndexPath:indexPath];
    } else {
        cell = (QSWaterFallCollectionViewCell*)[collectionViews dequeueReusableCellWithReuseIdentifier:@"QSWaterFallCollectionViewCell" forIndexPath:indexPath];
        
    }
    return cell;
}

@end
