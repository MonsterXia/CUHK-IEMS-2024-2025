<div align=center>
    <hr/>
    <h3>1155xxxxxx-Documentation</h3>
    <strong>
        IEMS 5725 Blockchain and Applications<br/>
        (Fall, 2024-25)
    </strong>
    <hr/>
</div>
<div align=center>
	Signature(s)<u>________________</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Date<u>_______________12/12/2024_____________</u><br>
    Name(s)<u>___YOUR, NAME____</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Student ID(s)<u>________1155xxxxxx___________</u><br>
    Course code<u>___IEMS5725___</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Course title<u>__Blockchain And Applications__</u><br>
</div>

# Documentation
[TOC]
## Run

1. Open [Remix](https://remix.ethereum.org/), new a file called 1155xxxxxx-codes.sol under contracts. Copy and paste all the codes in.
2. Click "Solidity Compiler", click "Compile 1155xxxxxx-codes.sol" to compile.
3. Click "Deploy & run transactions", click "Deploy" to run.
4. Contract can be seen under "Deployed Contracts", unfolded to use.

## Use

### Basic Requirements (Unit: ether)

**Buyer Registration**

- **buyerResgistration**: Registed or update your profile by putting in your name, email, shipping address.
- **viewProfile**: View your profile detail information such as Name, Email, Shipping Address.

**Seller Registration**

- **sellerResgistration**: Pay some money to become the seller. 
- **addProduct**: Adding the goods as seller by putting in goods' name, price, URL, inventory.   
- **editProduct**: Edit the goods as seller by putting in goods' id, name, price, URL, inventory.

**Shopping Moment**

- **viewProduct**: View the product's detail information by putting in product's id.
- **addToWishList**: Add item to your own wish list by putting in product's id.

**Transaction Initiation**

- **transactionInitiation**: Initialized the transaction by paying some money and putting in product's id and amount to buy.

**Transaction Information**

- **viewTransaction**: View the transaction's detail information by putting in transaction's id.

**Return Request**

- **returnRequest**: Change the transaction's status to return by putting in transaction's id.
- **approveRequset**: Seller approve the transaction depending on the the transaction's status by putting in transaction's id.

**Transaction Completion**

- **completeTransaction**: User end the transaction by putting in transaction's id.

### Bonus Feature

**Trading with ERC20 Token**

​	Use **buyToken** and **sellToken** to exchange between your token and ethers with a fixed rate.

​	Using **ERCsellerResgistration**,  **ERCtransactionInitiation**, **ERCapproveRequset**, **ERCcompleteTransaction** instead of **sellerResgistration**, **transactionInitiation**, **approveRequset**, **completeTransaction**, respectively.

- **buyToken**: Use Ether to buy tokens.
- **sellToken**: Sell tokens to the Ether back.
- **ERCsellerResgistration**: ERC20's version. Pay some tokens to become the seller.
- **ERCtransactionInitiation**: ERC20's version. Initialized the transaction by paying some tokens and putting in product's id and amount to buy.
- **ERCapproveRequset**: ERC20's version. Seller approve the transaction depending on the the transaction's status by putting in transaction's id.
- **ERCcompleteTransaction**: ERC20's version. User end the transaction by putting in transaction's id.

**Seller Penalty**

​	If seller don't approve return Request in 1 days, payer can call **punishment** to punished the seller for some money.

​	Using **fixedRetrurnRequest** instead of **retrurnRequest**.

- **fixedReturnRequest**: Change the transaction's status to return and also record the return time by putting in transaction's id.
- **punishment**: User can punish the seller if seller didn't approve return in time by putting in transaction's id.

<div align=center>
    <strong>- End -</strong>
</div>
