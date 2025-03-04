// SPDX-License-Identifier: GPL-3.0
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

pragma solidity ^0.8.6;

contract Retail{
    struct UserData {
        string name;
        string email;
        string shippingAddress;
        bool isRegistered;
    }   // User
    struct Product {
        string name;
        uint256 price;
        string URL;
        uint256 inventory;
    }   // Product
    struct Transaction {
        uint32 productID;
        uint256 productAmount;
        uint256 cost;
        address buyer;
        bool isApproved;
        uint16 transactionStatus;
    } // Transaction

    bool hasSeller;                             // If seller has registerd
    address sellerAddress;                      // Seller's address
    uint256 cryptocurrency;                     // Temp money store in the contract
    uint32 productCount;                        // Total product added by seller
    uint256 transactionCount;                   // Total transactions

    mapping(address => UserData) users;         // <user, user Profile>
    mapping(uint32 => Product) products;        // <product ID, product>
    mapping(address => uint32[]) wishlist;      // <user, wishlist>
    mapping(uint256 => Transaction) txs;        // <tx-id, tx>

    /**
    * Buyer Registration
    */

    /**
    * Registed or update your profile by putting in your name, email, shipping address.
    * [at]param _name
    * User's name
    * [at]param _email
    * User's email
    * [at]param _shippingAddress
    * User's deliverary address
    * [at]return
    * If successfully registed or update your profile
    */
    function buyerResgistration(string memory _name, string memory _email, string memory _shippingAddress) public returns (string memory){
        string memory returnSting;
        // The seller can't be a buyer
        require(sellerAddress != msg.sender, "The seller can't be a buyer");
        if (users[msg.sender].isRegistered) {
            // If registered, only allow tp update the name and shippingAddress
            users[msg.sender].name = _name;
            users[msg.sender].shippingAddress = _shippingAddress;

            returnSting = "You have update your profile.";
        }else {
            // If not registered
            users[msg.sender] = UserData(_name, _email, _shippingAddress, true);
            returnSting = "You have successfully registed.";
        }

        return returnSting;
    }

    /**
    * View your profile detail information such as Name, Email, Shipping Address.
    * [at]return
    * Your profile detail information
    */
    function viewProfile() public view returns(string memory) {
        // make sure user is registed
        require(users[msg.sender].isRegistered, "You havn't registed yet.");
        return string(abi.encodePacked(
            "Name: ", users[msg.sender].name, ", ",
            "Email: ", users[msg.sender].email, ", ",
            "Shipping Address: ", users[msg.sender].shippingAddress
        ));
    }

    /**
     * Seller Registration
    */

    /**
    * Pay some money to become the seller.
    */
    function sellerResgistration() public payable {
        // Already has the seller, not allowed
        require(!hasSeller, "Already has the seller");

        // require deposite to Resgist
        require(msg.value > 0,"You must send some deposite to become seller");
        
        // Resgist
        sellerAddress = msg.sender;
        hasSeller = true;
        cryptocurrency += msg.value;
    }

    /**
    * Adding the goods as seller by putting in goods' name, price, URL, inventory.
    * [at]param _name
    * Product's name
    * [at]param _price
    * Product's price
    * [at]param _URL
    * Product's URL
    * [at]param _inventory
    * Product's inventory
    */
    function addProduct(string memory _name, uint256 _price, string memory _URL, uint256 _inventory) public {
        // only seller can add
        require(msg.sender ==  sellerAddress,"You're not the seller!");
        // add the product and update the total products number
        products[productCount] = Product(_name,_price,_URL,_inventory);
        productCount++;
    }

    /**
    * Edit the goods as seller by putting in goods' id, name, price, URL, inventory.
    * [at]param _ProductID
    * Product's ID
    * [at]param _name
    * Product's name
    * [at]param _price
    * Product's price
    * [at]param _URL
    * Product's URL
    * [at]param _inventory
    * Product's inventory
    */
    function editProduct(uint32 _ProductID, string memory _name, uint256 _price, string memory _URL, uint256 _inventory) public {
        // only seller can add
        require(msg.sender ==  sellerAddress,"You're not the seller!");
        // id too large, no such id
        require(_ProductID < productCount, "There's no product match your id, please add first!");
        // update the info
        products[_ProductID] = Product(_name,_price,_URL,_inventory);
    }

    /**
     * Shopping Moment
    */

    /**
    * View the product's detail information by putting in product's id.
    * [at]param _ProductID
    * Product's ID
    * [at]return
    * Product's name
    * Product's price
    * Product's URL
    * Product's inventory
    */
    function viewProduct(uint32 _ProductID)public view returns(string memory, uint256, string memory, uint256) {
        // id too large, no such id
        require(_ProductID < productCount, "There's no product match your id");
        return (products[_ProductID].name, products[_ProductID].price, products[_ProductID].URL, products[_ProductID].inventory);
    }

    /**
    * Add item to your own wish list by putting in product's id.
    * [at]param _ProductID
    * Product's ID
    */
    function addToWishList(uint32 _ProductID) public {
        wishlist[msg.sender].push(_ProductID);
    }

    /**
     * Transaction Initiation
    */

    // Assum: status of Transaction
    //      0 -> none
    //      1 -> initiation
    //      2 -> return
    //      3 -> complete

    /**
    * Initialized the transaction by paying some money and putting in product's id and amount to buy.
    * [at]param _ProductID
    * Product's ID
    * [at]param _amount
    * Product's amount to buy
    */
    function transactionInitiation(uint32 _productID, uint256 _amount) public payable{
        // calculate the total cost
        uint256 totalcost = products[_productID].price*_amount;
        // not enough money
        require(msg.value >= totalcost, "You don't have pay the enough money.");
        // make transaction
        txs[transactionCount] = Transaction(_productID, _amount, totalcost, msg.sender, false, 1);
        transactionCount++;
    }
    
    /**
     * Transaction Information
    */

    /**
    * View the transaction's detail information by putting in transaction's id.
    * [at]param _transactionID
    * Transaction's ID
    * [at]return
    * Transaction's ID
    * Product's ID
    * Product's amount
    * Transaction's cost
    * Buyer's email
    * Is transaction approved
    * Transaction's status
    */
    function viewTransaction(uint256 _transactionID) public view returns(uint256, uint32, uint256, uint256, string memory, bool, string memory){
        // for buyer, txs can only be seen by themselves
        if (msg.sender != sellerAddress) {
            require(txs[_transactionID].buyer == msg.sender, "This Tx not belongs to you.");
        }

        // change the stutes' int to String
        string memory status;
        if (txs[_transactionID].transactionStatus == 0) {
            require(true, "Transaction hasn't been initialized yet.");
        }else if (txs[_transactionID].transactionStatus == 1) {
            status = "Initialized";
        }else if (txs[_transactionID].transactionStatus == 2) {
            status = "Return";
        }else if (txs[_transactionID].transactionStatus == 3) {
            status = "Approve";
        }else if (txs[_transactionID].transactionStatus == 4) {
            status = "Complete";
        }else{
            require(true, "Transaction status invalid.");
        }

        return(_transactionID, txs[_transactionID].productID, txs[_transactionID].productAmount, txs[_transactionID].cost, users[msg.sender].email, txs[_transactionID].isApproved, status);
    }

    /**
     * Return Request
    */

    /**
    * Change the transaction's status to return by putting in transaction's id.
    * [at]param _transactionID
    * Transaction's ID
    */
    function returnRequest(uint256 _transactionID) public {
        require(txs[_transactionID].buyer == msg.sender, "This Tx not belongs to you.");
        // only initialized tx can be return
        require(txs[_transactionID].transactionStatus == 1, "Current status can't be changed to return.");

        // change the status to return
        txs[_transactionID].transactionStatus = 2;
    }

    /**
    * Seller approve the transaction depending on the the transaction's status by putting in transaction's id.
    * [at]param _transactionID
    * Transaction's ID
    */
    function approveRequset(uint256 _transactionID) public {
        require(msg.sender == sellerAddress, "Only seller can approve.");
        // only return tx and initialized tx can be approve
        require(txs[_transactionID].transactionStatus == 1 || txs[_transactionID].transactionStatus == 2, "You don't need to approve.");

        // approve
        txs[_transactionID].isApproved = true;
        if (txs[_transactionID].transactionStatus == 2) {
            // return, pay back
            payable(txs[_transactionID].buyer).transfer(txs[_transactionID].cost * 1 ether);
        } else {
            // inintialized, send goods
            products[txs[_transactionID].productID].inventory -= txs[_transactionID].productAmount;
        }
    }

    /**
     * Transaction Completion
    */

    /**
    * User end the transaction by putting in transaction's id.
    * [at]param _transactionID
    * Transaction's ID
    */
    function completeTransaction(uint256 _transactionID) public {
        require(txs[_transactionID].buyer == msg.sender, "This Tx not belongs to you.");
        // only approved can be end
        require(txs[_transactionID].isApproved, "This Tx has not been approved");

        if (txs[_transactionID].transactionStatus == 1) {
            // not returned ,seller get money
            payable(sellerAddress).transfer(txs[_transactionID].cost  * 1 ether);
        }
        // set status to complete
        txs[_transactionID].transactionStatus = 3;
    }

    /*
    * Bonus Feature
    */

    /*
    * Trading with ERC20 Token
    *
    */

    mapping(address => uint256) accountERC;     // account for store the Token
    uint256 public exchangeRate = 1 ether;      // fixed rate for exchange between tokens and ethers 

    /**
    * Use Ether to buy tokens.
    * [at]param _tokenAmount
    * Amount of token to buy
    */
    function buyToken(uint256 _tokenAmount) payable public {
        require(msg.value == _tokenAmount * exchangeRate, "Incorrect ETH amount");
        accountERC[msg.sender] += _tokenAmount;
    }

    /**
    * Sell tokens to the Ether back.
    * [at]param _tokenAmount
    * Amount of token to sell
    */
    function sellToken(uint256 _tokenAmount) public {
        require(accountERC[msg.sender] >= _tokenAmount, "You don't have enough token");
        accountERC[msg.sender] -= _tokenAmount;
        payable(msg.sender).transfer(_tokenAmount * exchangeRate);
    }

    /**
    * ERC20's version. Pay some tokens to become the seller.
    */
    function ERCsellerResgistration(uint256 _deposit) public{
        // Already has the seller, not allowed
        require(!hasSeller, "Already has the seller");

        // You dont't have enough token
        require(accountERC[msg.sender] >= _deposit, "Already has the seller");

        // require deposite to Resgist
        require(_deposit > 0,"You must send some deposite to become seller");
        
        // Resgist
        sellerAddress = msg.sender;
        hasSeller = true;
        cryptocurrency += _deposit;

        // Update ERC account
        accountERC[msg.sender] -= _deposit;
    }

    /**
    * ERC20's version. Initialized the transaction by paying some tokens and putting in product's id and amount to buy.
    * [at]param _ProductID
    * Product's ID
    * [at]param _amount
    * Product's amount to buy
    */
    function ERCtransactionInitiation(uint32 _productID, uint256 _amount) public {
        // calculate the total cost
        uint256 totalcost = products[_productID].price*_amount;
        // not enough token
        require(accountERC[msg.sender] >= totalcost, "You don't have pay the enough money.");
        // make transaction
        txs[transactionCount] = Transaction(_productID, _amount, totalcost, msg.sender, false, 1);
        transactionCount++;

        // update user's token account
        accountERC[msg.sender] -= totalcost;
    }

    /**
    * ERC20's version. Seller approve the transaction depending on the the transaction's status by putting in transaction's id.
    * [at]param _transactionID
    * Transaction's ID
    */
    function ERCapproveRequset(uint256 _transactionID) public {
        require(msg.sender == sellerAddress, "Only seller can approve.");
        // only return tx and initialized tx can be approve
        require(txs[_transactionID].transactionStatus == 1 || txs[_transactionID].transactionStatus == 2, "You don't need to approve.");

        // approve
        txs[_transactionID].isApproved = true;
        if (txs[_transactionID].transactionStatus == 2) {
            // return, pay tokens back
            accountERC[txs[_transactionID].buyer] += txs[_transactionID].cost;
        } else {
            // inintialized, send goods
            products[txs[_transactionID].productID].inventory -= txs[_transactionID].productAmount;
        }
    }

    /**
    * ERC20's version. User end the transaction by putting in transaction's id.
    * [at]param _transactionID
    * Transaction's ID
    */
    function ERCcompleteTransaction(uint256 _transactionID) public {
        require(txs[_transactionID].buyer == msg.sender, "This Tx not belongs to you.");
        // only approved can be end
        require(txs[_transactionID].isApproved, "This Tx has not been approved");

        if (txs[_transactionID].transactionStatus == 1) {
            // not returned ,seller get tokens
            accountERC[sellerAddress] += txs[_transactionID].cost;
        }
        // set status to complete
        txs[_transactionID].transactionStatus = 3;
    }

    /*
    * Seller Penalty
    */
    uint public constant lockTime = 1 days;
    mapping(uint256 => uint) public ReturnActionTime;
    
    /**
    * Change the transaction's status to return and also record the return time by putting in transaction's id.
    * [at]param _transactionID
    * Transaction's ID
    */
    function fixedReturnRequest(uint256 _transactionID) public {
        require(txs[_transactionID].buyer == msg.sender, "This Tx not belongs to you.");
        // only initialized tx can be return
        require(txs[_transactionID].transactionStatus == 1, "Current status can't be changed to return.");
        // // change the status to return and record the timestamp
        txs[_transactionID].transactionStatus = 2;
        ReturnActionTime[_transactionID] = block.timestamp;
    }

    /**
    * User can punish the seller if seller didn't approve return in time by putting in transaction's id.
    * [at]param _transactionID
    * Transaction's ID
    */
    function punishment(uint256 _transactionID) public {
        // reach the time limit, can punish
        require(block.timestamp >= ReturnActionTime[_transactionID] + lockTime, "Lock time has not passed yet");
        txs[_transactionID].cost += 0.000005 ether;
    }
}
