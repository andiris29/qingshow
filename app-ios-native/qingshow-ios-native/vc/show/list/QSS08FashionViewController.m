//
//  QSS08FashionViewController.m
//  qingshow-ios-native
//
//  Created by ching show on 15/4/29.
//  Copyright (c) 2015年 QS. All rights reserved.
//

#import "QSS08FashionViewController.h"
#import "QSS08FashionCollectionViewCell.h"
static NSString *Indentifier = @"QSS08FashionCollectionViewCellIdentifier";
@interface QSS08FashionViewController ()<UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout>

@property (nonatomic, strong)QSS08FashionCollectionViewCell *QSS08Cell;
@property (nonatomic, strong)QSFashionCollectionViewProvider *provider;

@end

@implementation QSS08FashionViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = @"时尚情报";
    // Do any additional setup after loading the view from its nib.
    self.collectionView.delegate = self;
    self.collectionView.dataSource = self;
    [self.collectionView registerNib:[UINib nibWithNibName:@"QSS08FashionCollectionViewCell" bundle:nil] forCellWithReuseIdentifier:Indentifier];
    
}

#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section
{
    return 10;
}

// The cell that is returned must be retrieved from a call to -dequeueReusableCellWithReuseIdentifier:forIndexPath:
- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    self.QSS08Cell = [self.collectionView dequeueReusableCellWithReuseIdentifier:Indentifier forIndexPath:indexPath];
    return self.QSS08Cell;
}



- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

#pragma mark - UICollectionViewDelegate
- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath{
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
