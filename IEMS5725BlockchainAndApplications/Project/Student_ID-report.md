<div align=center>
    <hr/>
    <h3>Your_Student_ID-Report</h3>
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

# Report

[TOC]

## a. advantages of using blockchain in retail transactions (10 points)

- In retail, most important thing is Transparency. Blockchain provides a transparent and immutable record of transactions. This makes all transaction records visible, preventing manual modifications to the records. 

- At the same time, blockchain often use distributed services. For distributed services, even if some data is lost due to uncontrollable factors, other hosts in the network can still ensure the security of the data.

- Through smart contracts, intermediaries such as banks and other payment processors are eliminated, reducing corresponding fees and thus eliminating the impact which is on normal transactions when third-party institutions encounter issues.

- Blockchain's inherent nature provides transparency and traceability for the supply chain in retail. This enables operators to easily track and manage their information on all selling items, which provids a more reliable guarantee of for them to sell high-quality goods.

- By implementing  the conversion of various assets into tokens and meeting the requirements of relevant protocols such as ERC20, the use of tokens can further improve the internal liquidity and also reduce transaction costs. In that way there can reduce the costs for operators.

## b. problems of the current project and give some potential solutions (15 points)

- For seller, once the seller has registed, this address can be changed. If someone lose his account or just say for better management, a company may have various accounts, how to abolish the deprecated account address and update it to the currently active address to synchronize the records becomes a problem.
	- We can utilize key pairs by storing a public key in the contract. The seller keeps one or a set of private keys(Achieve by multi-signature methods etc..). By using encryption algorithms like RSA, if the public key matches the private key, it confirms the identity of the seller, allowing for the modification of the seller's account address.
- In the process defined within the project, for Transaction Completion, money is transferred to the seller's wallet only when the user confirms completion. However, in actual retail scenarios, if post-sale situations are considered, this process may not fully reflect the real state. For instance, if a customer discovers that the product quality is poor and wishes to return it, but the order is already immutable, this can pose a challenge.

	- In reality, if the system does not automatically confirm orders as complete upon receipt, users may often forget to do so, leading to delays in funds reaching the merchant's account. To address this issue and accommodate return scenarios, it would be advisable to implement an automatic confirmation mechanism after around two weeks, for example.

		For returns, once the merchant approves the return, the user can initiate the return process. At this point, appropriate methods should be provided to increase the merchant's inventory (as the system typically decreases inventory upon approval) and to refund either partially or fully depending on the circumstances. Finally, the entire process can be completed either through the automatic confirmation mechanism or manual confirmation by the user.

- For orders, inventory should only decrease when the merchant approves the shipment as follow the project. However, during the period between when a user submits an order and the merchant approves the shipment, the inventory remains unchanged. This can lead to situations where the total demand exceeds the available inventory.

	For instance, if the total inventory is 5 and the merchant delays approval, customers continue to see the inventory as 5. In this scenario, if more than 5 customers (e.g., 6 customers) each purchase 1 item, the total demand of 6 exceeds the available inventory, causing issues.

	- To address this issue effectively, it is advisable to maintain two separate sets of inventory lists: one for displayed order inventory and the other for actual inventory levels. Both sets should be initialized similarly.

		The inventory displayed to users should reflect the order inventory and should update in real-time as orders are placed. On the other hand, the inventory managed by the merchant for logistics purposes should remain based on the original inventory levels.

		During the completion of orders, the changes in both lists should be synchronized. However, during the order updating process, the latter list (actual inventory) may update slightly slower than the former list (order inventory). The order inventory list reflects the demand, while the actual inventory list reflects the supply levels. 

<div align=center>
    <strong>- End -</strong>
</div>
