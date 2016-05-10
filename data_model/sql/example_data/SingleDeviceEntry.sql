insert into brand_name(id,name) values(4,'LG');
insert into device_model(brand_id, name, marketing_name, id) values(4,'GT405','Viewty GT',4);
insert into device_os(id, name, is_deprecated, last_modified) values (1, '',false, now());
insert into device_os_version(id, os_id, version, minor_version, major_version, is_deprecated, last_modified) values (1, 1, '', 0.0, 0.0, false, now());
insert into capabilities(id, type, description, wurfl_capability, lookup_value, inverted_match, use_for_population, is_deprecated, last_modified) values(1, 'type', 'Click to SMS Capability', 'xhtml_send_sms_string', 'none', true, true, false, now());
insert into device_types(id, name, priority, description, is_deprecated, last_modified) values(1, 'is_feature_phone', 1, 'Definition of feature phones', false, now());
insert into device_browser(id,name) values(2,'Teleca-Obigo');
insert into device_browser_version(browser_id,version,id) values(2,'7.1',4);
insert into device_mapping (id,source_id) values (4,'lg_gt405_ver1_suborange');
update device_mapping set source = 'ScientiaMobile-1.0',brand_id = 4, model_id = 4,capabilities_json = '{"xhtml_make_phone_call_string":"tel:","xhtml_send_sms_string":"sms:","max_image_width":"228","max_image_height":"360","nokia_series":"0","resolution_width":"240","resolution_height":"400","max_url_length_in_requests":"256","accept_third_party_cookie":"true","is_transcoder":"false","mobile_browser":"Teleca-Obigo","mobile_browser_version":"7.1","built_in_camera":"false","has_qwerty_keyboard":"false","pointing_method":"","j2me_midp_1_0":"true","j2me_midp_2_0":"true","video":"false","device_os_version":"","device_os":"","post_method_support":"true"}',is_bot = false,is_smart_phone = false,os_version_id = 1,os_id = 1,supported_banners = '{}',capabilities_list = '{1}',device_type_id = 1,resolution_width = 240,resolution_height = 400,is_tablet = false,is_wireless_device = true,browser_id = 2,browser_version_id = 4,last_modified = now() where id = 4;

