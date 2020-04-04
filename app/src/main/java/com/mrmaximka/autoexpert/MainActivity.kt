package com.mrmaximka.autoexpert

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.mrmaximka.autoexpert.model.UserModel
import com.mrmaximka.autoexpert.ui.start.StartFragment
import kotlinx.android.synthetic.main.alert_collizion_account.view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_start.*


class MainActivity : AppCompatActivity(), RewardedVideoAdListener {

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var database: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    private lateinit var db: FirebaseFirestore
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private lateinit var userModel: UserModel
    private lateinit var valueEventListener: ValueEventListener
    private var isReewarded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this, "ca-app-pub-6381695679948997~7206919219")
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar.title = ""
        toolbar.subtitle = ""
//        supportFragmentManager.primaryNavigationFragment
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }



        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        loadRewardedVideoAd()
        activity_items_btn_add.setOnClickListener { showAdd() }


    }

    private fun showAdd() {
        if (mRewardedVideoAd.isLoaded) {
            mRewardedVideoAd.show()
        }
    }



    private fun loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
            AdRequest.Builder().build())
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("MMV", "Google sign in failed", e)
                // ...
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
//        google_sign_in_btn.setOnClickListener { signIn() }
        val currentUser = auth.currentUser
        if (currentUser == null){
//            signIn()
            singAnonymous()
        }
        else{
            updateUI(currentUser)
        }
    }

    private fun singAnonymous() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MMV", "signInAnonymously:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("MMV", "signInAnonymously:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

            }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d("MMV", "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.currentUser?.linkWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("MMV", "linkWithCredential:success")
                    val user = task.result?.user
                    updateUI(user)

                }else if((task.exception as FirebaseAuthException?)!!.errorCode == "ERROR_CREDENTIAL_ALREADY_IN_USE"){
                    Log.d("MMV", "Choose account")
                    val dialog = Dialog(this)
                    val factory = LayoutInflater.from(this)
                    val view = factory.inflate(R.layout.alert_collizion_account, null)
                    view.alert_account_cancel.setOnClickListener {
                        googleSignInClient.signOut()
                        dialog.onBackPressed()
                    }

                    view.alert_account_load_old.setOnClickListener {
                        val oldUid = auth.uid
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("MMV", "signInWithCredential:success")
                                    myRef.child(oldUid!!).removeEventListener(valueEventListener)
                                    val user = auth.currentUser
                                    updateUI(user)
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("MMV", "signInWithCredential:failure", task.exception)
                                    Toast.makeText(applicationContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                                    updateUI(null)
                                }

                                // ...
                            }
                        dialog.onBackPressed()
                    }

                    dialog.setContentView(view)
                    dialog.show()

                }else {

                    Log.w("MMV", "linkWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }

                // ...
            }

    }


    private fun updateUI(user: FirebaseUser?) {
        if (user != null){

            val dialog = Dialog(this)
            val factory = LayoutInflater.from(this)
            val view = factory.inflate(R.layout.alert_loading, null)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setContentView(view)
            dialog.show()
            if (user.displayName != null ) Log.d("MMV", user.displayName!!)
            if (user.email != null ) Log.d("MMV", user.email!!)
            if (user.phoneNumber != null ) Log.d("MMV", user.phoneNumber!!)
            if (user.phoneNumber != null ) Log.d("MMV", user.phoneNumber!!)
            Log.d("MMV", user.uid)
            Log.d("MMV", user.providerId)
            database = FirebaseDatabase.getInstance()
            myRef = database.reference
            userModel = UserModel.instance
            userModel.setUid(user.uid)

            userModel.needToLogin.observe(this, Observer {
                it?.let {
                    if (userModel.needToLogin.value == true){
                        userModel.needToLogin.value = false
                        signIn()
                    }
                }
            })

            if(user.email != null && user.email!!.isNotEmpty()){
                if (user.displayName != null && user.displayName!!.isNotEmpty()){
                    userModel.setName(user.displayName!!)
                }else if (user.providerData[1].displayName != null && user.providerData[1].displayName!!.isNotEmpty()){
                    userModel.setName(user.providerData[1].displayName!!)
                }

                userModel.email.value = user.email
            }

            valueEventListener = object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = dataSnapshot.value
                    Log.d("MMV", "Value is: $value")
                    if (value == null){
                        createAccount(myRef, user, dialog)
                    }else{
                        loadAccount(myRef, user, dataSnapshot, dialog)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    dialog.cancel()
                    Toast.makeText(applicationContext, "Ошибка подключение к базе данных. Проверьте соединение", Toast.LENGTH_LONG).show()
                    // Failed to read value
                    Log.w("MMV", "Failed to read value.", error.toException())
                }
            }
            myRef.child(user.uid).addValueEventListener(valueEventListener)

            Log.d("MMV", "Added")
        }
    }

    private fun createAccount(
        myRef: DatabaseReference,
        user: FirebaseUser,
        dialog: Dialog
    ) {
        val map: HashMap<String, Int> = HashMap()
        map["coins"] = 0
        myRef.child(user.uid).setValue(map)
        val list: ArrayList<Int> = ArrayList()
        for(i in 0..100){
            list.add(0)
        }
        myRef.child(user.uid).child("parts").push()
        myRef.child(user.uid).child("parts").setValue(list)
        Log.d("MMV", "Account created")
        dialog.cancel()
    }

    private fun loadAccount(
        myRef: DatabaseReference,
        user: FirebaseUser,
        dataSnapshot: DataSnapshot,
        dialog: Dialog
    ) {
        val objectsGTypeInd: GenericTypeIndicator<HashMap<String?, Any?>?> =
            object : GenericTypeIndicator<HashMap<String?, Any?>?>() {}
        val objectHashMap: HashMap<String?, Any?>? =
            dataSnapshot.getValue(objectsGTypeInd)


        if (objectHashMap != null){
            val objectArrayList: ArrayList<Any?> = ArrayList(objectHashMap.values)
            val getParts = objectHashMap["parts"]
            if (getParts != null){
                var partsList: ArrayList<Int> = ArrayList()
                partsList = objectHashMap.getValue("parts") as ArrayList<Int>
                userModel.setParts(partsList)
            }

            val coins = objectArrayList[0]
            Log.d("MMV", "$coins")
            userModel.setCoins(coins.toString())

            main_coins.text = coins.toString()
        }

        dialog.cancel()

    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onPause() {
        super.onPause()
        mRewardedVideoAd.pause(this)
    }

    override fun onResume() {
        super.onResume()
        mRewardedVideoAd.resume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRewardedVideoAd.destroy(this)
    }

    override fun onRewarded(reward: RewardItem) {
        Log.d("MMV", "onRewarded! currency: ${reward.type} amount: ${reward.amount}")
        myRef.child(userModel.getUid()).child("coins").setValue(userModel.getCoins() + reward.amount)
        isReewarded = true
        /*Toast.makeText(this, "onRewarded! currency: ${reward.type} amount: ${reward.amount}",
            Toast.LENGTH_SHORT).show()*/
        // Reward the user.
    }

    override fun onRewardedVideoAdLeftApplication() {
        Log.d("MMV", "onRewardedVideoAdLeftApplication")
//        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdClosed() {
        loadRewardedVideoAd()
        Log.d("MMV", "onRewardedVideoAdClosed")
        if (isReewarded){
            main_add_coins.visibility = View.VISIBLE
            main_add_coins.animate()
                .alpha(0.0F)
                .setListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        main_add_coins.visibility = View.INVISIBLE
                    }
                })
                .duration = 1500
            isReewarded = false
        }
//        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        Log.d("MMV", "onRewardedVideoAdFailedToLoad")
//        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLoaded() {
        Log.d("MMV", "onRewardedVideoAdLoaded")
//        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
        Log.d("MMV", "onRewardedVideoAdOpened")
//        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoStarted() {
        Log.d("MMV", "onRewardedVideoStarted")
//        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
        Log.d("MMV", "onRewardedVideoCompleted")
//        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }
}
