#!/usr/bin/env python3
import argparse
import pathlib
import xml.etree.ElementTree as ET


ANDROID_NS = "http://schemas.android.com/apk/res/android"
ET.register_namespace("android", ANDROID_NS)


def android_name(name):
    return f"{{{ANDROID_NS}}}{name}"


def has_named(parent, tag, name):
    for child in parent.findall(tag):
        if child.get(android_name("name")) == name:
            return True
    return False


def add_text(parent, tag, attributes):
    child = ET.SubElement(parent, tag)
    for key, value in attributes.items():
        child.set(key, value)
    return child


def insert_before_application(manifest, child):
    children = list(manifest)
    for index, item in enumerate(children):
        if item.tag == "application":
            manifest.remove(child)
            manifest.insert(index, child)
            return


def add_billing_manifest_entries(manifest):
    if not has_named(manifest, "uses-permission", "com.android.vending.BILLING"):
        permission = add_text(manifest, "uses-permission", {
            android_name("name"): "com.android.vending.BILLING",
        })
        insert_before_application(manifest, permission)

    queries = manifest.find("queries")
    if queries is None:
        queries = ET.SubElement(manifest, "queries")
        insert_before_application(manifest, queries)
    existing_actions = set()
    for action in queries.findall("intent/action"):
        value = action.get(android_name("name"))
        if value:
            existing_actions.add(value)
    for action_name in (
        "com.android.vending.billing.InAppBillingService.BIND",
        "com.google.android.apps.play.billingtestcompanion.BillingOverrideService.BIND",
    ):
        if action_name not in existing_actions:
            intent = ET.SubElement(queries, "intent")
            add_text(intent, "action", {android_name("name"): action_name})

    application = manifest.find("application")
    if application is None:
        raise SystemExit("Manifest does not contain <application>.")

    if not has_named(application, "meta-data", "com.google.android.play.billingclient.version"):
        add_text(application, "meta-data", {
            android_name("name"): "com.google.android.play.billingclient.version",
            android_name("value"): "9.0.0",
        })

    for activity_name in (
        "com.android.billingclient.api.ProxyBillingActivity",
        "com.android.billingclient.api.ProxyBillingActivityV2",
    ):
        if not has_named(application, "activity", activity_name):
            add_text(application, "activity", {
                android_name("name"): activity_name,
                android_name("configChanges"): "keyboard|keyboardHidden|screenLayout|screenSize|orientation",
                android_name("exported"): "false",
                android_name("theme"): "@android:style/Theme.Translucent.NoTitleBar",
            })


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("manifest")
    args = parser.parse_args()

    path = pathlib.Path(args.manifest)
    tree = ET.parse(path)
    add_billing_manifest_entries(tree.getroot())
    tree.write(path, encoding="unicode", xml_declaration=False)


if __name__ == "__main__":
    main()
