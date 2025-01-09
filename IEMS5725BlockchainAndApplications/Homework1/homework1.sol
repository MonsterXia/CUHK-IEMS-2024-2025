// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

// Use of AI
/*
    I have not used any AI tool in this assignment.
*/

// Declaration:
/*
    I declare that the assignment here submitted is original
    except for source material explicitly acknowledged,
    and that the same or closely related material has not been
    previously submitted for another course.
    I also acknowledge that I am aware of University policy and
    regulations on honesty in academic work, and of the disciplinary
    guidelines and procedures applicable to breaches of such
    policy and regulations, as contained in the website.
    *
    University Guideline on Academic Honesty:
    http://www.cuhk.edu.hk/policy/academichonesty/

    Student Name : Your Name
    Student ID   : Student ID
    Class/Section: IEMS 5725
    Date         : 19/10/2024
*/

/* References used
    https://solidity-by-example.org/hacks
*/


// Answer for Q1

// In the case that playerA has decided his choices, if his choices was received and wrote in plaintext. 
// Since the tx is broadcasted, another player can known the choices before his making choice.
// In that way, another player can organize answer based on that and win. playerA will never win in the case.


// Answer for Q3

// Although we achieved encryption in the previous step, in this scenario, there are only four possible inputs, resulting in four values after hashing. 
// These values can still be derived through exhaustive search. 
// Therefore, it is necessary to provide a private key(salt) along with the input choice value for encryption, so that the entire set can be encrypted together.
// This way, the other player cannot reverse-engineer the plaintext from the hashed result.

contract Jungle{
    address address1;
    address address2;
    uint8 involved;

    enum Choice {Lion, Wolf, Cat, Rat}

    bytes32 hidden1;
    bytes32 hidden2;

    bool hidden1flag;
    bool hidden2flag;
    
    struct decision {
        Choice choice1;
        Choice choice2;
        bool revealFlag;
    }

    decision decision1;
    decision decision2;

    uint256 public startTime;

    address owner;
    constructor() {
      owner = msg.sender;
    }

    function register() public payable {
        require(msg.value == 1 ether, "You must send 1 ether to register.");
        if (involved == 0){
            address1 = msg.sender;
            involved = 1;
        } else if (involved == 1){
            require(address1 != msg.sender, "You have already register in.");
            address2 = msg.sender;
            involved = 2;
        } else {
            // send money back
            if (address1 == msg.sender || address2 == msg.sender){
                require(false, "You have already register in.");
            }
            require(false, "The maximum number of participants has been reached.");
        }
    }

    function commit(uint8 choice1, uint8 choice2, string memory hashString) public{
        require(address1 == msg.sender || address2 == msg.sender , "You're not involved in the game.");
        if (address1 == msg.sender){
            require(hidden1flag == false, "You have already commit, you can't do it again.");
            hidden1 = sha256(abi.encode(choice1, choice2, hashString));
            hidden1flag = true;
        }else {
            require(hidden2flag == false, "You have already commit, you can't do it again.");
            hidden2 = sha256(abi.encode(choice1, choice2, hashString));
            hidden2flag = true;
        }
    }

    function reveal(uint8 choice1, uint8 choice2, string memory hashString) public payable returns(bool){
        // make sure all of players have committed
        require(address1 == msg.sender || address2 == msg.sender , "You're not involved in the game.");
        require(hidden1flag == true && hidden2flag == true, "Someone has not committed yet, you can't reveal.");
        bool inputValid = true;

        // choice out of the 4 cards or two choice are same
        if (choice1 == choice2){
            inputValid = false;
        }

        if (choice1 < 0 && choice1 > 3){
            inputValid = false;
        }

        if (choice2 < 0 && choice2 > 3){
            inputValid = false;
        }
        
        if (address1 == msg.sender){
            require(decision1.revealFlag == false, "You have already revealed, you can only reveal once.");
            // lie
            if (sha256(abi.encode(choice1, choice2, hashString)) != hidden1){
                inputValid = false;
            }
            
            if (inputValid == false){
                // Player1 invalid, reset and punish
                involved = 0;
                startTime = 0;
                hidden1flag = false;
                hidden2flag = false;
                decision1.revealFlag = false;
                decision2.revealFlag = false;

                payable(address2).transfer(address(this).balance);
                return false;
            }else{
                decision1.choice1 = Choice(choice1);
                decision1.choice2 = Choice(choice2);
                decision1.revealFlag = true;
            }
        }else {
            require(decision2.revealFlag == false, "You have already revealed, you can only reveal once.");
            // lie
            if (sha256(abi.encode(choice1, choice2, hashString)) != hidden2){
                inputValid = false;
            }

            if (inputValid == false){
                // Player2 invalid, reset and punish
                involved = 0;
                startTime = 0;
                hidden1flag = false;
                hidden2flag = false;
                decision1.revealFlag = false;
                decision2.revealFlag = false;

                payable(address1).transfer(address(this).balance);
                return false;
            }else{
                decision2.choice1 = Choice(choice1);
                decision2.choice2 = Choice(choice2);
                decision2.revealFlag = true;
            }
        }

        if (startTime == 0){
            startTime = block.timestamp;
        }
        return true;
    }

    function winning() public payable returns(address){
        // At least one has revealed
        require(decision1.revealFlag == true || decision2.revealFlag == true, "None of the players have completed the reveal input.");

        if (decision1.revealFlag == true && decision2.revealFlag == true){
            require(owner == msg.sender, "Only owner can check winning.");

            // All player is honest
            uint8 winCount1 = winCountFunc(decision1.choice1, decision2.choice1) + winCountFunc(decision1.choice2, decision2.choice2);
            uint8 winCount2 = winCountFunc(decision2.choice1, decision1.choice1) + winCountFunc(decision2.choice2, decision1.choice2);
            
            involved = 0;
            startTime = 0;
            hidden1flag = false;
            hidden2flag = false;
            decision1.revealFlag = false;
            decision2.revealFlag = false;
            
            if (winCount1 > winCount2) {
                payable(address1).transfer(address(this).balance);
                return address1;
            }else if(winCount1 < winCount2){
                payable(address2).transfer(address(this).balance);
                return address2;
            }else {
                uint amount = address(this).balance / 2;
                payable(address1).transfer(amount);
                payable(address2).transfer(amount);
                address address0;
                return address0;
            }
        }else {
            // One of player decided not to reveal, force someone win after 2 mins
            require(block.timestamp - startTime >= 120, "A player wins only if another player does not reveal secret within 2 minutes of his revealing it.");

            if (decision1.revealFlag == true) {
                require(address1 == msg.sender, "Only the one revealed can call winning");

                involved = 0;
                startTime = 0;
                hidden1flag = false;
                hidden2flag = false;
                decision1.revealFlag = false;
                decision2.revealFlag = false;
                payable(address1).transfer(address(this).balance);
                return address1;
            }else{
                require(address2 == msg.sender, "Only the one revealed can call winning");

                involved = 0;
                startTime = 0;
                hidden1flag = false;
                hidden2flag = false;
                decision1.revealFlag = false;
                decision2.revealFlag = false;
                payable(address2).transfer(address(this).balance);
                return address2;
            }
        }
    }

    function winCountFunc(Choice _choiceA, Choice _choiceB)private pure returns(uint8){
        uint8 choiceA = uint8(_choiceA);
        uint8 choiceB = uint8(_choiceB);
        // only Lion->Wolf, Wolf->Cat, Cat->Rat, Rat->Lion
        if( 
            choiceA == 0 &&  choiceB == 1 ||
            choiceA == 1 &&  choiceB == 2 ||
            choiceA == 2 &&  choiceB == 3 ||
            choiceA == 3 &&  choiceB == 0 
        ){
            return 1;
        }else {
            return 0;
        }
    }
}